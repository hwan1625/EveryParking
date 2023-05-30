import config from "./config.js";

function drawMap() {
    let spotNum = -1;
    const parkingMap = document.getElementById('parking-map');
    for(let j=0; j < 6; j++) {
        const col = document.createElement('div');

        for (let i = 0; i < 19; i++) {
            const spot = document.createElement('div');
            const modal = document.createElement('div');
            spot.className = 'spot map-col btn btn-primary unoccupied';

            let num = i+j*19;
            if ((num >= 19 && num <= 37) || (num >= 76 && num <= 94) ||
                (num >= 38 && num <= 40) || (num >= 57 && num <= 59) ||
                (num >= 54 && num <= 56) || (num >= 73 && num <= 75) ||
                (num >= 95 && num <= 97) || [16,111,9,104,2].includes(num)) {
                spot.className = 'spot';
            } else {
                spot.setAttribute('data-bs-toggle', 'modal');
                spot.setAttribute('data-bs-target', `#modal-info-${++spotNum}`)
                spot.textContent = config.spotMap[spotNum];
                spot.id = `info-${spotNum}`;

                // 모달 요소 생성
                modal.classList.add('modal', 'fade');
                modal.id = `modal-info-${spotNum}`;
                modal.setAttribute('data-bs-backdrop', 'static');
                modal.setAttribute('data-bs-keyboard', 'false');
                modal.setAttribute('tabindex', '-1');
                modal.setAttribute('aria-labelledby', `target-${spotNum}`);
                modal.setAttribute('aria-hidden', 'true');

                // 모달 다이얼로그 생성
                const modalDialog = document.createElement('div');
                modalDialog.classList.add('modal-dialog', 'modal-dialog-centered', 'modal-dialog-scrollable');

                // 모달 콘텐츠 생성
                const modalContent = document.createElement('div');
                modalContent.classList.add('modal-content');

                // 모달 헤더 생성
                const modalHeader = document.createElement('div');
                modalHeader.classList.add('modal-header');

                // 모달 제목 생성
                const modalTitle = document.createElement('h1');
                modalTitle.classList.add('modal-title', 'fs-5', `modal-header-title-${spotNum}`);
                modalTitle.id = `target-${spotNum}`;
                modalTitle.textContent = `${spotNum}번 자리`;

                // 모달 닫기 버튼 생성
                const closeButton = document.createElement('button');
                closeButton.type = 'button';
                closeButton.classList.add('btn-close');
                closeButton.setAttribute('data-bs-dismiss', 'modal');
                closeButton.setAttribute('aria-label', 'Close');

                // 모달 본문 생성
                const modalBody = document.createElement('div');
                modalBody.classList.add('modal-body', `modal-header-content-${spotNum}`);

                const modalBodyUserInfo = document.createElement('div');
                modalBodyUserInfo.classList.add('modal-body-userInfo');
                modalBodyUserInfo.textContent = "";

                const userInfoUserId = document.createElement('p');
                userInfoUserId.className = `modal-body-userId`;
                const userInfoUserName = document.createElement('p');
                userInfoUserName.className = `modal-body-userName`;

                modalBodyUserInfo.append(userInfoUserId);
                modalBodyUserInfo.append(userInfoUserName);

                const modalBodyCarInfo = document.createElement('div');
                modalBodyCarInfo.classList.add('modal-body-carInfo');

                const modalBodyTimeInfo = document.createElement('div');
                modalBodyTimeInfo.classList.add('modal-body-timeInfo');


                modalBody.appendChild(modalBodyTimeInfo);
                modalBody.appendChild(modalBodyUserInfo);
                modalBody.appendChild(modalBodyCarInfo);

                // 모달 푸터 생성
                const modalFooter = document.createElement('div');
                modalFooter.classList.add('modal-footer');

                // 모달 이해 버튼 생성
                const understandButton = document.createElement('button');
                understandButton.type = 'button';
                understandButton.classList.add('btn', 'btn-danger', `info-violation-${spotNum}`);
                understandButton.setAttribute('data-bs-dismiss', 'modal');
                understandButton.textContent = '위약';


                // 모달 구조 조립
                modalHeader.appendChild(modalTitle);
                modalHeader.appendChild(closeButton);

                modalFooter.appendChild(understandButton);

                modalContent.appendChild(modalHeader);
                modalContent.appendChild(modalBody);
                modalContent.appendChild(modalFooter);

                modalDialog.appendChild(modalContent);

                modal.appendChild(modalDialog);

                // 모달을 body에 추가
                document.body.appendChild(modal);
            }
            col.appendChild(spot);
            col.appendChild(modal);
        }
        parkingMap.insertBefore(col, parkingMap.firstChild);
    }
}

export default drawMap;