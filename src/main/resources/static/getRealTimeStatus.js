import config from './config.js'
import addEntryLogItem from './addEntryLogItem.js';
import addReportLogItem from './addReportLogItem.js';

const EntryLogListContainer = document.getElementById("logStatus-container");
const ReportLogListContainer = document.getElementById("reportStatus-container");

// 소켓 연결
let socket, stompClient;
socket = new WebSocket(`ws://${config.ip}/ws/log`);
stompClient = Stomp.over(socket);
stompClient.connect({}, function() {
    // 실시간 출입 기록
    stompClient.subscribe('/topic/entry-log', function (response) {
        let log = JSON.parse(response.body);
        const existLogItem = document.getElementById(`entryLog-item-${log.id}`) ?? null;
        const toastLive = document.getElementById('liveToast');
        const logToastTitle = document.getElementById('logToast-title');
        const logToastContent = document.getElementById('logToast-content');
        if (existLogItem !== null) {
            const updatedExitTime = existLogItem.querySelector('.exit-time');
            updatedExitTime.textContent =
                new Date(log.exitTime).toLocaleString(
                    'ko-KR',
                    {
                        year: 'numeric',
                        month: '2-digit',
                        day: '2-digit',
                        hour: '2-digit',
                        minute: '2-digit',
                        second: '2-digit'
                    }
                );
            const accordionButton = existLogItem.querySelector('.accordion-button');
            accordionButton.style.color = "red";

            logToastTitle.textContent = '반납';
            logToastTitle.style.color = 'red';
            logToastContent.textContent = `${log.carNumber}(이)가 출차하였습니다.`;
            const toast = new bootstrap.Toast(toastLive);
            toast.show();

        } else {
            EntryLogListContainer.insertBefore(addEntryLogItem(log), EntryLogListContainer.firstChild);

            logToastTitle.textContent = '승인';
            logToastTitle.style.color = 'blue';
            logToastContent.textContent = `${log.carNumber}(이)가 입차하였습니다.`;
            const toast = new bootstrap.Toast(toastLive);
            toast.show();
        }
    });

    // 실시간 신고 현황
    stompClient.subscribe('/topic/report-log', function (response) {
        let log = JSON.parse(response.body);
        console.log(log);

        const existLogItem = document.getElementById(`reportLog-item-${log.id}`) ?? null;
        const toastLive = document.getElementById('liveToast');
        const logToastTitle = document.getElementById('logToast-title');
        const logToastContent = document.getElementById('logToast-content');

        if (existLogItem !== null) {
            const accordionButton = existLogItem.querySelector('.accordion-button');
            accordionButton.style.color = "red";

            logToastTitle.textContent = '신고 처리 완료';
            logToastTitle.style.color = 'red';
            logToastContent.textContent = `신고가 처리되었습니다.`;
            const toast = new bootstrap.Toast(toastLive);
            toast.show();

        } else {
            ReportLogListContainer.insertBefore(addReportLogItem(log), ReportLogListContainer.firstChild);

            logToastTitle.textContent = '신고 접수';
            logToastTitle.style.color = 'blue';
            logToastContent.textContent = `신고가 접수되었습니다.`;
            const toast = new bootstrap.Toast(toastLive);
            toast.show();
        }

    });


    // 실시간 좌석 현황
    stompClient.subscribe('/topic/parking-status', function (response) {
        let info = JSON.parse(response.body);

        const infoElement = document.getElementById(`info-${info.parkingId}`);
        const modalElement = document.getElementById(`modal-info-${info.parkingId}`);
        const modalHeaderTitle = modalElement.querySelector(`.modal-header-title-${info.parkingId}`);
        const modalBodyUserId = modalElement.querySelector(`.modal-body-userId`);
        const modalBodyUserName = modalElement.querySelector(`.modal-body-userName`);
        const modalBodyCarInfo = modalElement.querySelector(`.modal-body-carInfo`);
        const modalBodyTimeInfo = modalElement.querySelector(`.modal-body-timeInfo`);

        const toastLive = document.getElementById('liveToast');
        const logToastTitle = document.getElementById('logToast-title');
        const logToastContent = document.getElementById('logToast-content');

        const violationButton = modalElement.querySelector(`.info-violation-${info.parkingId}`)

        if (info.parkingStatus === 'AVAILABLE') {
            infoElement.className = 'spot map-col btn btn-primary unoccupied';
            modalBodyUserId.textContent = "";
            modalBodyUserName.textContent = "";
            modalBodyCarInfo.textContent = "";
            modalBodyTimeInfo.textContent = "";

        } else if (info.parkingStatus === 'USED') {
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

            logToastTitle.textContent = '배정';
            logToastTitle.style.color = 'red';
            logToastContent.textContent = `${info.details.carNumber}(이)가 ${config.spotMap[info.parkingId-1]}번에 배정되었습니다.`;
            const toast = new bootstrap.Toast(toastLive);
            toast.show();
        }
    });

    // 실시간 유저 위약 갱신
    stompClient.subscribe('/topic/user-violation', function (response) {
        let status = JSON.parse(response.body);
        const searchResultContainer = document.getElementById('search-result');
        const resultUserStatus = searchResultContainer.querySelector('.search-result-userStatus');

        const toastLive = document.getElementById('liveToast');
        const logToastTitle = document.getElementById('logToast-title');
        const logToastContent = document.getElementById('logToast-content');

        resultUserStatus.textContent = `위약상태: ${status.memberStatus}`;
        if (status.memberStatus === 'DEFAULT') {
            logToastTitle.textContent = '위약 해제';
            logToastTitle.style.color = 'blue';
            logToastContent.textContent = `${status.userId} 님의 위약 처리가 해제 되었습니다.`;
        } else if (status.memberStatus === 'FORBIDDEN') {
            logToastTitle.textContent = '위약 처리';
            logToastTitle.style.color = 'red';
            logToastContent.textContent = `${status.userId} 님을 위약 처리 하였습니다.`;
        }
        const toast = new bootstrap.Toast(toastLive);
        toast.show();
    });
});
socket.onclose = function () {
    console.log('WebSocket 연결이 닫혔습니다.');
};
socket.onerror = function (error) {
    console.error('WebSocket 에러:', error);
};