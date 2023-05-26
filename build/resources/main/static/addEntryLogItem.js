import config from "./config.js";

function addEntryLogItem(log) {
    // accordion-item 요소 생성

    const accordionItem = document.createElement('div');
    accordionItem.className = 'accordion-item';
    accordionItem.id = `entryLog-item-${log.id}`;

    // accordion-header 요소 생성
    const accordionHeader = document.createElement('h2');
    accordionHeader.className = 'accordion-header';
    accordionHeader.id = `heading-entryLog-${log.id}`;

    // accordion-button 요소 생성
    const accordionButton = document.createElement('button');
    accordionButton.className = 'accordion-button collapsed';
    accordionButton.type = 'button';
    accordionButton.setAttribute('data-bs-toggle', 'collapse');
    accordionButton.setAttribute('data-bs-target', `#collapse-entryLog-${log.id}`);
    accordionButton.setAttribute('aria-expanded', 'false');
    accordionButton.setAttribute('aria-controls', `collapse-entryLog-${log.id}`);

    if (log.exitTime) {
        accordionButton.style.color = "red";
    } else {
        accordionButton.style.color = "blue";
    }

    // 첫 번째 span 요소 생성 (+ 아이콘)
    const spanPlus = document.createElement('span');
    spanPlus.className = 'simple-time';
    let simpleTime = new Date(log.entryTime);
    spanPlus.textContent = `${simpleTime.getHours().toString().padStart(2, '0')}:${simpleTime.getMinutes().toString().padStart(2, '0')}`;

    // 두 번째 span 요소 생성 (차량 번호)
    const spanPlateNumber = document.createElement('span');
    spanPlateNumber.textContent = decodeURIComponent(log.carNumber);

    // span 요소들을 accordion-button에 추가
    accordionButton.appendChild(spanPlus);
    accordionButton.appendChild(spanPlateNumber);

    // accordion-header에 accordion-button 추가
    accordionHeader.appendChild(accordionButton);

    // accordion-collapse 요소 생성
    const accordionCollapse = document.createElement('div');
    accordionCollapse.className = 'accordion-collapse collapse';
    accordionCollapse.id = `collapse-entryLog-${log.id}`;
    accordionCollapse.setAttribute('aria-labelledby', `heading-entryLog-${log.id}`);
    accordionCollapse.setAttribute('data-bs-parent', '#logStatus-container');

    // accordion-body 요소 생성
    const accordionBody = document.createElement('div');
    accordionBody.className = 'accordion-body';

    const lpImageDiv = document.createElement('div');
    lpImageDiv.className = 'lp-image-div';

    const image = document.createElement('img');
    // img-fluid rounded
    image.className = 'rounded img-thumbnail';
    let formattedTime = log.entryTime.replace(/[-:T.]/g, '-');
    image.src = `http://${config.ip}/images/${log.carNumber}_${formattedTime}.jpg`;
    image.alt = '';

    lpImageDiv.appendChild(image);

    const entryExitTimeDiv = document.createElement('div');
    entryExitTimeDiv.className = 'entry-exit-time';

    // 입장 시간 요소 생성
    const entryTimeParagraph = document.createElement('p');
    entryTimeParagraph.className = 'entry-time';
    let entryTime = new Date(log.entryTime);
    entryTimeParagraph.textContent = entryTime.toLocaleString(
        'ko-KR',
        {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit'
        });

    // 퇴장 시간 요소 생성
    const exitTimeParagraph = document.createElement('p');
    exitTimeParagraph.className = 'exit-time';
    if (log.exitTime) {
        let exitTime = new Date(log.exitTime);
        exitTimeParagraph.textContent = exitTime.toLocaleString(
            'ko-KR',
            {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit'
            });
    } else {
        exitTimeParagraph.textContent = "";
    }
    const exitButtonContainer = document.createElement("div");
    exitButtonContainer.className = "exit-btn-container";

    const exitButton = document.createElement('button');
    exitButton.className = 'btn btn-danger btn-sm exit-btn';
    exitButton.type = 'button';
    exitButton.textContent = '강제 출차';
    exitButton.setAttribute('lp-str', log.carNumber);

    exitButton.addEventListener('click', (e) => {
        let lpStr = e.target.getAttribute('lp-str');
        fetch(`http://${config.ip}/api/exit?number=${lpStr}`, {method: 'GET', headers: {'Content-Type': 'application/json'}})
            .then(response => {
                console.log(response.status);
            })
            .catch(error => {
                console.error('Error:', error);
            });
    });

    exitButtonContainer.appendChild(exitButton);

    // 입장 시간과 퇴장 시간 요소를 entry-exit-time div에 추가
    entryExitTimeDiv.appendChild(entryTimeParagraph);
    entryExitTimeDiv.appendChild(exitTimeParagraph);
    entryExitTimeDiv.appendChild(exitButtonContainer);

    accordionBody.appendChild(lpImageDiv);
    accordionBody.appendChild(entryExitTimeDiv);

    // accordion-body를 accordion-collapse에 추가
    accordionCollapse.appendChild(accordionBody);

    // accordion-header와 accordion-collapse를 accordion-item에 추가
    accordionItem.appendChild(accordionHeader);
    accordionItem.appendChild(accordionCollapse);

    return accordionItem;
}

export default addEntryLogItem;