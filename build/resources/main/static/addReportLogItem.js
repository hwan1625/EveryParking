import config from "./config.js";

function addReportLogItem(log) {
    // accordion-item 요소 생성

    const accordionItem = document.createElement('div');
    accordionItem.className = 'accordion-item';
    accordionItem.id = `reportLog-item-${log.id}`;

    // accordion-header 요소 생성
    const accordionHeader = document.createElement('h2');
    accordionHeader.className = 'accordion-header';
    accordionHeader.id = `heading-repotLog-${log.id}`;

    // accordion-button 요소 생성
    const accordionButton = document.createElement('button');
    accordionButton.className = 'accordion-button collapsed';
    accordionButton.type = 'button';
    accordionButton.setAttribute('data-bs-toggle', 'collapse');
    accordionButton.setAttribute('data-bs-target', `#collapse-repotLog-${log.id}`);
    accordionButton.setAttribute('aria-expanded', 'false');
    accordionButton.setAttribute('aria-controls', `collapse-repotLog-${log.id}`);

    if (log.reportStatus === "COMPLETE") {
        accordionButton.style.color = "red";
    } else if (log.reportStatus === "WAITING") {
        accordionButton.style.color = "blue";
    }

    // 첫 번째 span 요소 생성 (+ 아이콘)
    const spanPlus = document.createElement('span');
    spanPlus.className = 'simple-time';
    let simpleTime = new Date(log.createdTime);
    spanPlus.textContent = `${simpleTime.getHours().toString().padStart(2, '0')}:${simpleTime.getMinutes().toString().padStart(2, '0')}`;

    // 두 번째 span 요소 생성 (사용자 ID)
    const spanUserId = document.createElement('span');
    spanUserId.textContent = log.title;

    // span 요소들을 accordion-button에 추가
    accordionButton.appendChild(spanPlus);
    accordionButton.appendChild(spanUserId);

    // accordion-header에 accordion-button 추가
    accordionHeader.appendChild(accordionButton);

    // accordion-collapse 요소 생성
    const accordionCollapse = document.createElement('div');
    accordionCollapse.className = 'accordion-collapse collapse';
    accordionCollapse.id = `collapse-repotLog-${log.id}`;
    accordionCollapse.setAttribute('aria-labelledby', `heading- reportLog-${log.id}`);
    accordionCollapse.setAttribute('data-bs-parent', '#reportStatus-container');

    // accordion-body 요소 생성
    const accordionBody = document.createElement('div');
    accordionBody.className = 'accordion-body';

    // 유저 body
    const reportUserBody = document.createElement('div');
    reportUserBody.className = 'report-user-body';
    const reportUserId = document.createElement('span');
    reportUserId.className = 'report-userId';
    reportUserId.textContent = `작성자: ${log.userInfo.userId}`;
    // const reportUserName = document.createElement('span');
    // reportUserName.className = 'report-userName';
    // reportUserName.textContent = `${log.userInfo.userName}`;
    reportUserBody.appendChild(reportUserId);
    // reportUserBody.appendChild(reportUserName);

    // 신고 내용 body
    const reportInfoBody = document.createElement('div');
    reportInfoBody.className = 'report-info-body';
    const reportTitle = document.createElement('p');
    reportTitle.className = 'report-title';
    reportTitle.textContent = `제목: ${log.title}`;
    const reportContent = document.createElement('p');
    reportContent.className = 'report-content';
    reportContent.textContent = `내용: ${log.contents}`;
    reportInfoBody.appendChild(reportTitle);
    reportInfoBody.appendChild(reportContent); 

    // 이미지 body
    const reportImageDiv = document.createElement('div');
    const image = document.createElement('img');
    reportImageDiv.className = 'report-image-div';
    image.className = 'report-image rounded img-thumbnail';
    image.src = `http://${config.ip}/images/${log.files.storeFileName}`;
    image.alt = '이미지 누락';
    
    reportImageDiv.appendChild(image);


    const reportCancelButtonContainer = document.createElement("div");
    reportCancelButtonContainer.className = "rerpot-cancel-btn-container";

    const reportCancelButton = document.createElement('button');
    reportCancelButton.className = 'btn btn-danger btn-sm'
    reportCancelButton.type = 'button';
    reportCancelButton.textContent = '신고취소';
    reportCancelButton.id = `report-cancel-btn-${log.id}`

    // 신고 취소 이벤트 생성
    reportCancelButton.addEventListener('click', (e) => {
        fetch(`http://${config.ip}/api/report-cancel`, {
            method: 'GET', 
            headers: {
                'Content-Type': 'application/json',
                'reportId': log.id,
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

    reportCancelButtonContainer.appendChild(reportCancelButton);

    accordionBody.appendChild(reportUserBody);
    accordionBody.appendChild(reportInfoBody);
    accordionBody.appendChild(reportImageDiv);
    accordionBody.appendChild(reportCancelButtonContainer);

    // accordion-body를 accordion-collapse에 추가
    accordionCollapse.appendChild(accordionBody);

    // accordion-header와 accordion-collapse를 accordion-item에 추가
    accordionItem.appendChild(accordionHeader);
    accordionItem.appendChild(accordionCollapse);

    return accordionItem;
}

export default addReportLogItem;