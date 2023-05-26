// 좌우 슬라이드 너비 조정
const container = document.querySelector('#wrap');
const resizeBar = document.querySelector('.resize-bar');
let isResizing = false;
let prevX = 0;

resizeBar.addEventListener('mousedown', startResize);

function startResize(e) {
    isResizing = true;
    prevX = e.clientX;
    container.classList.add('resizing');
    document.addEventListener('mousemove', resize);
    document.addEventListener('mouseup', stopResize);
}

function resize(e) {
    if (!isResizing) return;
    
    const deltaX = e.clientX - prevX;
    const sectionA = container.querySelector('#parkingLotStatus-section');
    const sectionB = container.querySelector('#slider');
    
    const sectionAWidth = sectionA.offsetWidth - deltaX;
    const sectionBWidth = sectionB.offsetWidth + deltaX;
    
    let screenWidth = window.innerWidth;
    
    // 각 섹션의 최소 너비를 확인하고 최소 너비보다 작으면 조정합니다.
    if (sectionAWidth < screenWidth*0.4 || sectionBWidth < screenWidth*0.3) {

        return;
    }
    
    sectionA.style.width = `${sectionAWidth}px`;
    sectionB.style.width = `${sectionBWidth}px`;
    
    prevX = e.clientX;
}

function stopResize() {
    isResizing = false;
    container.classList.remove('resizing');
    document.removeEventListener('mousemove', resize);
    document.removeEventListener('mouseup', stopResize);
}

// 신고 현황 슬라이드-업
const toggleButton = document.getElementById('reportSection-toggle');
const commentContent = document.getElementById('reportStatus-section');
toggleButton.addEventListener('click', () => {
      commentContent.classList.toggle('slide-up');
});