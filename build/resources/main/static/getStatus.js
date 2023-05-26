import config from './config.js'
import drawMap from './drawMap.js';
import addEntryLogItem from './addEntryLogItem.js';
import addReportLogItem from './addReportLogItem.js';

// 전체 출입 리스트 불러오기
const EntryLogListContainer = document.getElementById("logStatus-container");
fetch(`http://${config.ip}/api/entry-log`, {method: 'GET', headers: {'Content-Type': 'application/json'}})
    .then(response => response.json())
    .then(data => {
        data.forEach(log => {
            EntryLogListContainer.appendChild(addEntryLogItem(log));
        });
    })
    .catch(error => {
        console.error('Error:', error);
    });

// 전체 신고 리스트 불러오기
const ReportLogListContainer = document.getElementById("reportStatus-container");
fetch(`http://${config.ip}/api/report-log`, {method: 'GET', headers: {'Content-Type': 'application/json'}})
    .then(response => response.json())
    .then(data => {
        data.forEach(log => {
            ReportLogListContainer.appendChild(addReportLogItem(log));
        });
    })
    .catch(error => {
        console.error('Error:', error);
    });

// 주차장 맵 그리기
drawMap();
// 주차장 좌석 현황 불러오기
fetch(`http://${config.ip}/api/map/1`, {method: 'GET', headers: {'Content-Type': 'application/json'}})
    .then(response => response.json())
    .then(data => {
        data.forEach(info => {
            // console.log(info.parkingId);
            const infoElement = document.getElementById(`info-${info.parkingId}`);
            const modalElement = document.getElementById(`modal-info-${info.parkingId}`);
            const modalHeaderTitle = modalElement.querySelector(`.modal-header-title-${info.parkingId}`);
            const modalBodyUserId = modalElement.querySelector(`.modal-body-userId`);
            const modalBodyUserName = modalElement.querySelector(`.modal-body-userName`);
            const modalBodyCarInfo = modalElement.querySelector(`.modal-body-carInfo`);
            const modalBodyTimeInfo = modalElement.querySelector(`.modal-body-timeInfo`);
            const violationButton = modalElement.querySelector(`.info-violation-${info.parkingId}`)
            
            
            if (info.parkingStatus === 'USED') {
                // 위약처리 이벤트 생성
                violationButton.addEventListener('click', (e) => {
                    fetch(`http://${config.ip}/api/violation`, {
                        method: 'GET', 
                        headers: {
                            'Content-Type': 'application/json',
                            'userId': info.details.member.userId
                        }
                    })
                        .then(response => {
                            if (response.ok) {
                                return response.text();
                            } else {
                                throw new Error("Failed to violate user");
                            }
                            })
                        .catch(error => {
                            console.error('Error:', error);
                        });
                });
                infoElement.className = 'spot map-col btn btn-primary occupied';
                modalBodyUserId.textContent = `${info.details.member.userId}`;
                modalBodyUserName.textContent = `${info.details.member.userName}`;
                modalBodyCarInfo.textContent = `${info.details.carNumber}`;
                let currentTime = new Date();
                modalBodyTimeInfo.textContent = `${currentTime.getHours().toString().padStart(2, '0')}:${currentTime.getMinutes().toString().padStart(2, '0')}`
            }
        });
    })
    .catch(error => {
        console.log('Error:', error);
    })

// 회원 정보 조회
const searchFindByOption = document.getElementById('search-findBy-option');
const searchSubmitButton = document.getElementById('search-submit-btn');
const searchResultContainer = document.getElementById('search-result');
const userViolationContainer = document.getElementById('user-violation-container');
const userViolationButton = document.getElementById('user-violation-register-btn');
const userUnViolationButton = document.getElementById('user-violation-unregister-btn');
searchSubmitButton.addEventListener('click', () => {
    const searchFindBy = searchFindByOption.querySelector('input[name="findBy"]:checked').value;
    const searchInput = document.querySelector('#search-input-container > input').value;
    const resultUserId = searchResultContainer.querySelector('.search-result-userId');
    const resultUserName = searchResultContainer.querySelector('.search-result-userName');
    const resultEmail = searchResultContainer.querySelector('.search-result-email');
    const resultStudentId = searchResultContainer.querySelector('.search-result-studentId');
    const resultPhoneNumber = searchResultContainer.querySelector('.search-result-phoneNumber');
    const resultCarNumber = searchResultContainer.querySelector('.search-result-carNumber');
    const resultModelName = searchResultContainer.querySelector('.search-result-modelName');
    const resultUserStatus = searchResultContainer.querySelector('.search-result-userStatus');

    let body;
    if (searchFindBy === 'userId') {
        body = {"value": searchInput, "option": 0};
    } else if (searchFindBy == 'carNumber') {
        body = {"value": searchInput, "option": 1};
    }
    fetch(`http://${config.ip}/api/search`, {
    method: "POST", 
    headers: {"Content-Type": "application/json"},
    body: JSON.stringify(body)
    })
        .then(response => response.json())
        .then(info => {
            console.log(info)
            searchResultContainer.querySelector('.search-no-result').style = 'display: none;';
            resultUserId.textContent = `아이디: ${info.userId}`;
            resultUserName.textContent = `이름: ${info.userName}`;
            resultEmail.textContent = `이메일: ${info.email}`;
            resultStudentId.textContent = `학번: ${info.studentId}`;
            resultPhoneNumber.textContent = `전화번호: ${info.phoneNumber}`;
            resultCarNumber.textContent = `차량번호: ${info.carNumber ?? "미등록"}`;
            resultModelName.textContent = `차량모델: ${info.modelName ?? "미등록"}`;
            resultUserStatus.textContent = `위약상태: ${info.memberStatus}`;
            userViolationContainer.style = 'display: flex;';
            
            userViolationButton.setAttribute("userId", info.userId);
            userUnViolationButton.setAttribute("userId", info.userId);
        })
        .catch(error => {
            searchResultContainer.querySelector('.search-no-result').style = 'display: block;';
            resultUserId.textContent = "";
            resultUserName.textContent = "";
            resultEmail.textContent = "";
            resultStudentId.textContent = "";
            resultPhoneNumber.textContent = "";
            resultCarNumber.textContent = "";
            resultModelName.textContent = "";
            resultUserStatus.textContent = "";
            userViolationContainer.style = 'display: none;';
        });
})

userViolationButton.addEventListener('click', (e) => {
    fetch(`http://${config.ip}/api/user/violation`, {
        method: "GET", 
        headers: {
            "Content-Type": "application/json",
            "userId": e.target.getAttribute("userId")
        }
    })
        .then(response => response.json())
        .then(data => {
            
        })
        .catch(error => {
            console.log(error);
        })
})

userUnViolationButton.addEventListener('click', (e) => {
    fetch(`http://${config.ip}/api/user/unviolation`, {
        method: "GET", 
        headers: {
            "Content-Type": "application/json",
            "userId": e.target.getAttribute("userId")
        }
    })
        .then(response => response.json())
        .then(data => {
            
        })
        .catch(error => {
            console.log(error);
        })
})