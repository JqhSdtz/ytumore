function isThisWeek(weekStr) {
    if (typeof weekStr === 'undefined' || weekStr === null || weekStr === '')
        return false;
    const weekParts = weekStr.split('|');
    if(weekParts.length === 1)//只有周次描述，没有周次值
        return parseWeekDescription(weekStr);
    return parseWeekValue(weekParts[1]);
}

function parseWeekValue(weekStr) {
    if(curWeekNum > weekStr.length)
        return false;
    return weekStr[curWeekNum - 1] === '1';
}

function parseWeekDescription(weekStr) {
    const reg = new RegExp(/\d+-\d+/);
    let weeks = weekStr.split(',');
    if(weeks.length === 0)
        weeks = weekStr.split('，');
    for(let i = 0; i < weeks.length; ++i) {
        const str = weeks[i];
        if (reg.test(str)) {
            const nums = reg.exec(str)[0].split('-');
            const s = parseInt(nums[0]), e = parseInt(nums[1]);
            if (curWeekNum >= s && curWeekNum <= e)
                return true;
            continue;
        } else {
            const num = parseInt(str);
            if (!isNaN(num) && curWeekNum === num)
                return true;
            continue;
        }
    }
    return false;
}


const courseColorMap = new Map();
const colorArray = ['#CCFFFF','#CCFF99','#FFCCCC','#99CC99','#99CCCC','#FFCC99','#FFCCCC','#CCCCFF','#FFFFCC','#CCFFFF'];
const notThisWeekColor = '#CCCCCC';
let curColorIdx = 0;

let slideMode = localStorage.getItem('ymslidemode') === '1' ? '1' : '0';//0是非滑动模式，1是滑动模式，默认0

function getColorByCName(cName) {
    if(courseColorMap.has(cName))
        return courseColorMap.get(cName);
    const color = colorArray[curColorIdx];
    curColorIdx = curColorIdx == colorArray.length - 1 ? 0 : curColorIdx + 1;
    courseColorMap.set(cName, color);
    return color;
}

function cpIdxToTdIdx(cpIdx) {
    return cpIdx.substr(0, cpIdx.length - 1);
}

function setCourseContent() {
    try {
        for (let i = 0; i < 12; ++i)
            for (let j = 0; j < 7; ++j) {
                const tdIdx = ((i + 1) * 10 + j + 1).toString();
                let cpCnt = 0;
                for (let s = 0; s < 5; ++s) {
                    const cpIdx = ((i + 1) * 100 + (j + 1) * 10 + s + 1).toString();
                    if (localMap.has(cpIdx)) {
                        ++cpCnt;
                        const cpJson = localMap.get(cpIdx);
                        if (cpJson.type === 'del' && $('#cp' + cpIdx).length !== 0)
                            delExistedCourse(tdIdx, cpIdx, true);
                        else
                            loadCourse(cpJson.cp, cpJson.type === 'add' ? true : false, true);
                        continue;
                    }
                    if ($('#cp' + cpIdx).length === 0)
                        continue;
                    loadCourse(loadCpObj(cpIdx, tdIdx, false), false, true);
                    ++cpCnt;
                }
                $('#cpTd' + tdIdx).attr('data-cpCnt', cpCnt);
            }
    } catch (e) {
        $('#errTip').text('加载课表失败，错误信息:' + e.message + '。若使用自定义课表，点击恢复默认课表可能会解决问题').show();
        $('#resetButton').show();
    }
}

function getPxOfStr(str) {
    str = str.substr(0, str.length - 2);
    return parseFloat(str);
}

function setCpHeight() {
    $('.coursePanel').css('height', 'auto');
    for (let i = 0; i < 12; ++i)
        for (let j = 0; j < 7; ++j) {
            const tdIdx = ((i + 1) * 10 + j + 1).toString();
            const cpCnt = parseInt($('#cpTd' + tdIdx).attr('data-cpCnt'));
            if(typeof cpCnt === 'undefined' || cpCnt === NaN ||  cpCnt === 0)
                continue;
            let cpSumHeight = 0;
            for(let s = 0; s < cpCnt - 1; ++s)
                cpSumHeight += getPxOfStr($('#cp' + ((i + 1) * 100 + (j + 1) * 10 + s + 1).toString()).css('height'));
            const tdHeight = (getPxOfStr($('#cpTd' + tdIdx).css('height')) - 0.3 * parseFloat(getRem()) -  cpSumHeight).toString();
            $('#cp' + ((i + 1) * 100 + (j + 1) * 10 + cpCnt).toString()).css('height', tdHeight + 'px');
        }
}

function addNewCourse(tdIdx) {
    const cpIdx = (tdIdx * 10 + 1).toString();
    $('#cName_modal').val('');
    $('#cTeacher_modal').val('');
    $('#room_modal').val('');
    $('#weeks_modal').val('');
    $('#cAttr_modal').val('');
    $('#cCredit_modal').val('');
    $('#cNote_modal').val('');
    $('#secNum_modal').val('1');
    $('#modalTitle').text('添加课程');
    $('#saveModBtn').unbind().click(function (ev) {
        const secNum = parseInt($('#secNum_modal').val());
        if (!validateSecNum(secNum, tdIdx, true)) {
            stopEventPropagation(ev);
            $('#confirmBtn').unbind();
            $('#confirmContent').text('添加失败！该课程占用了非法的小节。');
            $('#confirmModal').modal('show');
            return;
        }
        const cp = loadCpObj(cpIdx, tdIdx, true);
        saveCourseJson(cp, oriCourseSet.has(cpIdx) ? 'mod' : 'add');
        loadCourse(cp, true);
    });
    $('#delCourseBtn').hide();
    $('#customModal').modal('show');
}

function modExistedCourse(tdIdx, cpIdx) {
    $('#cName_modal').val($('#cName' + cpIdx).text());
    $('#cTeacher_modal').val($('#cTeacher' + cpIdx).text());
    $('#room_modal').val($('#room' + cpIdx).text());
    $('#weeks_modal').val($('#weeks' + cpIdx).text());
    $('#cAttr_modal').val($('#cAttr' + cpIdx).text());
    $('#cCredit_modal').val($('#cCredit' + cpIdx).text());
    $('#secNum_modal').val($('#secNum' + cpIdx).val());
    $('#cNote_modal').val($('#cNote' + cpIdx).text());
    $('#modalTitle').text('自定义课程');
    $('#delCourseBtn').show();
    $('#customModal').modal('show');
    $('#confirmContent').text('确认删除此课程？(删除后可通过恢复默认课程重新获得)');
    $('#confirmBtn').unbind().click(function () {
        $('#customModal').modal('hide');
        saveCourseJson({cpIdx: cpIdx}, 'del');
        delExistedCourse(tdIdx, cpIdx);
    });
    $('#saveModBtn').unbind().click(function (ev) {
        const secNum = parseInt($('#secNum_modal').val());
        if (!validateSecNum(secNum, tdIdx, false, cpIdx)) {
            stopEventPropagation(ev);
            $('#confirmBtn').unbind();
            $('#confirmContent').text('修改失败！该课程占用了非法的小节。');
            $('#confirmModal').modal('show');
            return;
        }
        const cp = loadCpObj(cpIdx, tdIdx, true);
        saveCourseJson(cp, oriCourseSet.has(cpIdx) ? 'mod' : 'add');
        loadCourse(cp, false);
    });
}

function loadCpObj(cpIdx, tdIdx, fromModal) {
    return {
        cpIdx: cpIdx,
        cNo: $('#cNo' + cpIdx).text(),
        cTeacher: fromModal ? $('#cTeacher_modal').val() : $('#cTeacher' + cpIdx).text(),
        weeks: fromModal ? $('#weeks_modal').val() : $('#weeks' + cpIdx).text(),
        cAttr: fromModal ? $('#cAttr_modal').val() : $('#cAttr' + cpIdx).text(),
        cCredit: fromModal ? $('#cCredit_modal').val() : $('#cCredit' + cpIdx).text(),
        cName: fromModal ? $('#cName_modal').val() : $('#cName' + cpIdx).text(),
        secNum: fromModal ? $('#secNum_modal').val() : $('#secNum' + cpIdx).val(),
        room: fromModal ? $('#room_modal').val() : $('#room' + cpIdx).text(),
        cNote: fromModal ? $('#cNote_modal').val() : $('#cNote' + cpIdx).text(),
        tdIdx: tdIdx
    }
}

function validateSecNum(secNum, tdIdx, isNew, cpIdx) {
    if (secNum > 1) {
        let tdRow = Math.floor(tdIdx / 10), tdCol = tdIdx % 10;
        if (tdRow <= 4 && tdRow + secNum - 1 > 4)
            return false;
        if (tdRow <= 8 && tdRow + secNum - 1 > 8)
            return false;
        if (tdRow <= 12 && tdRow + secNum - 1 > 12)
            return false;
        if (isNew) {
            for (let i = secNum - 1; i > 0; --i) {
                const tdEle = $('#cpTd' + ((tdRow + i) * 10 + tdCol).toString());
                if (tdEle.length === 0 || tdEle.children('input').length !== 0) {
                    return false;
                }
            }
            return true;
        } else {
            const oriSecNum = parseInt($('#secNum' + cpIdx).val());
            if (oriSecNum > secNum)
                return true;
            tdRow += oriSecNum
            for (let i = 0; i < secNum - oriSecNum; ++i) {
                const tdEle = $('#cpTd' + ((tdRow + i) * 10 + tdCol).toString());
                if (tdEle.length === 0 || tdEle.children('input').length !== 0) {
                    return false;
                }
            }
            return true;
        }
    } else
        return true;
}

function loadCourse(cp, isNew, isFromInit) {
    let oriSecNum;
    const cNo = cp.cNo === '' ? '自定义' : cp.cNo;
    const tdIdx = cpIdxToTdIdx(cp.cpIdx);
    $('#cpTd' + tdIdx).css('padding', '0.15rem');
    const cpContent = isNew ? $('<div id="cpContent' + cp.cpIdx + '" style="display: none"></div>') : $('#cpContent' + cp.cpIdx);
    cpContent.html('<p>课程号：<span id="cNo' + cp.cpIdx + '">' + cNo + '</span></p>'
        + '<p>教师：<span id="cTeacher' + cp.cpIdx + '">' + cp.cTeacher + '</span></p>'
        + '<p>周次：<span id="weeks' + cp.cpIdx + '" data-value="' + cp.weeks + '">' + cp.weeks.split('|')[0] + '</span></p>'
        + '<p>课程属性：<span id="cAttr' + cp.cpIdx + '">' + cp.cAttr + '</span></p>'
        + '<p>学分：<span id="cCredit' + cp.cpIdx + '">' + cp.cCredit + '</span></p>');
    const cpTd = $('#cpTd' + cp.tdIdx);
    cpTd.attr('rowspan', cp.secNum);
    if (isNew) {
        cpTd.append('<input id="secNum' + cp.cpIdx + '" type="hidden" value="' + cp.secNum + '"/>')
        cpTd.append(cpContent);
    } else {
        oriSecNum = parseInt($('#secNum' + cp.cpIdx).val());
        $('#secNum' + cp.cpIdx).val(cp.secNum);
    }
    const coursePanel = isNew ? $('<div id="cp' + cp.cpIdx + '" class="coursePanel" style="width:100%;"></div>') : $('#cp' + cp.cpIdx);
    coursePanel.attr('title', cp.cName);
    let isThisWeekStr = '';
    if (isThisWeek(cp.weeks)) {
        // if (cp.tdIdx % 10 - 1 === thisWeekDay)
        //     coursePanel.css('font-weight', 'bold');
        isThisWeekStr = '<span id="isThisWeek' + cp.cpIdx + '"class="text-success isThisWeek">[本周上]</span>';
        coursePanel.css('backgroundColor',getColorByCName(cp.cName));//更改课程颜色
    } else {
        isThisWeekStr = '<span id="isThisWeek' + cp.cpIdx + '"class="text-dark isThisWeek">[非本周]</span>';
        coursePanel.css('backgroundColor', notThisWeekColor);//更改课程颜色
    }
    coursePanel.html('<span id="cName' + cp.cpIdx + '">' + cp.cName + '</span></br>' +
        '<span id="room' + cp.cpIdx + '">' + cp.room + '</span></br>' +
        isThisWeekStr + '</br><span class="text-info" id="cNote' + cp.cpIdx + '">' + cp.cNote + '</span>');
    coursePanel.attr('data-content', cpContent.html());
    const _secNum = parseInt(cp.secNum);
    if (isNew) {
        if (_secNum > 1) {
            const tdRow = Math.floor(cp.tdIdx / 10), tdCol = cp.tdIdx % 10;
            for (let i = _secNum - 1; i > 0; --i) {
                $('#cpTd' + ((tdRow + i) * 10 + tdCol).toString()).hide();
            }
        }
    } else {
        if (_secNum > oriSecNum) {
            const tdRow = Math.floor(cp.tdIdx / 10) + oriSecNum, tdCol = cp.tdIdx % 10;
            for (let i = 0; i < _secNum - oriSecNum; ++i) {
                $('#cpTd' + ((tdRow + i) * 10 + tdCol).toString()).hide();
            }
        } else {
            const tdRow = Math.floor(cp.tdIdx / 10) + oriSecNum - 1, tdCol = cp.tdIdx % 10;
            for (let i = 0; i < oriSecNum - _secNum; ++i) {
                $('#cpTd' + ((tdRow - i) * 10 + tdCol).toString()).show();
            }
        }
    }
    if (isNew) {
        cpTd.append(coursePanel);
        cpTd.children('.addIcon').remove();
        cpTd.unbind();
    }
    if (!isFromInit)//不是来自初始化，则需手动添加编辑的ICON
        appendEditIcon(cp.tdIdx, cp.cpIdx, true);
}

function delExistedCourse(tdIdx, cpIdx, isFromInit) {
    const oriSecNum = $('#secNum' + cpIdx).val();
    const multi = $('#cpTd' + tdIdx).children('.coursePanel').length > 1 ? true : false;
    $('#secNum' + cpIdx).remove();
    $('#cpContent' + cpIdx).remove();
    $('#cp' + cpIdx).remove();
    const cpCnt =  parseInt($('#cpTd' + tdIdx).attr('data-cpCnt'));
    if(cpCnt && cpCnt !== NaN && cpCnt > 0)
        $('#cpTd' + tdIdx).attr('data-cpCnt', cpCnt - 1);
    if (multi)
        return;//本节同时有两个或以上课程则不清空，否则将该表格变为空
    $('#cpTd' + tdIdx).attr('rowspan', '1');
    if (!isFromInit)
        appendAddIcon(tdIdx, true);
    const tdRow = Math.floor(tdIdx / 10), tdCol = tdIdx % 10;
    for (let i = 1; i <= oriSecNum - 1; ++i)
        $('#cpTd' + ((tdRow + i) * 10 + tdCol).toString()).show();
}

function resetCourse() {
    localStorage.removeItem('ymcoursejson' + localStorage.getItem('ymuid'));
    window.location.reload();
}

function saveCourseJson(cp, type) {
    localMap.set(cp.cpIdx, {
        idx: cp.cpIdx,
        type: type,
        cp: cp
    });
}

function readCourse() {
    try {
        if (localStorage.getItem('ymcoursejson' + localStorage.getItem('ymuid')) != null)
            localJson = JSON.parse(localStorage.getItem('ymcoursejson' + localStorage.getItem('ymuid')));
        else if (localStorage.getItem('ymcoursejson') != null) {//为了兼容之前没改名字版本的存储内容
            localJson = JSON.parse(localStorage.getItem('ymcoursejson'));
            localStorage.setItem('ymcoursejson' + localStorage.getItem('ymuid'), localStorage.getItem('ymcoursejson'));
            localStorage.removeItem('ymcoursejson');
        } else
            localJson = {'uid': localStorage.getItem('ymuid'), 'cArray': []};
        const length = localJson.cArray.length;
        for (let i = 0; i < length; ++i)
            localMap.set(localJson.cArray[i].idx, localJson.cArray[i]);
    } catch (e) {
        $('#errTip').text('加载自定义课表失败，错误信息:' + e.message + ' (点击此处不再提示)').show();
    }
}

function saveModifiedTimeTable() {
    localJson.cArray = [];
    localMap.forEach(function (value) {
        localJson.cArray.push(value);
    });
    localStorage.setItem('ymcoursejson' + localStorage.getItem('ymuid'), JSON.stringify(localJson));
}

function initCustomMode() {
    for (let i = 0; i < 12; ++i)
        for (let j = 0; j < 7; ++j) {
            const tdIdx = ((i + 1) * 10 + j + 1).toString();
            if (typeof $('#cpTd' + tdIdx)[0] === 'undefined')
                continue;
            if ($('#cpTd' + tdIdx)[0].childElementCount === 0) {//本节为空
                appendAddIcon(tdIdx, false);
            } else {//本节不为空
                for (let s = 0; s < 5; ++s) {
                    const cpIdx = ((i + 1) * 100 + (j + 1) * 10 + s + 1).toString();
                    if ($('#cp' + cpIdx).length === 0)
                        continue;
                    appendEditIcon(tdIdx, cpIdx, false);
                }
            }
        }
}

function recordOriCourseTable() {
    for (let i = 0; i < 12; ++i)
        for (let j = 0; j < 7; ++j) {
            const tdIdx = ((i + 1) * 10 + j + 1).toString();
            if ($('#cpTd' + tdIdx)[0].childElementCount !== 0) {
                for (let s = 0; s < 5; ++s) {
                    const cpIdx = ((i + 1) * 100 + (j + 1) * 10 + s + 1).toString();
                    if ($('#cp' + cpIdx).length !== 0)
                        oriCourseSet.add(cpIdx);
                }
            }
        }
}

function appendAddIcon(tdIdx, show) {
    const display = show ? 'block' : 'none';
    $('#cpTd' + tdIdx).append($('<span class="addIcon icon-plus"></span>').css({
        display: display,
        width: '100%',
        fontSize: '1.5rem',
        textAlign: 'center',
        color: 'rgba(35, 51, 51, 0.1)'
    })).unbind().click(function () {
        //添加新课程
        addNewCourse(tdIdx);
    });
}

function appendEditIcon(tdIdx, cpIdx, show) {
    const display = show ? 'block' : 'none';
    $('#cp' + cpIdx).append($('<span class="editIcon icon-edit"></span>').css({
        display: display,
        width: '100%',
        marginTop: '0.5rem',
        fontSize: '1.5rem',
        textAlign: 'center',
        color: 'rgba(35, 51, 51, 0.1)'
    })).unbind().click(function () {
        modExistedCourse(tdIdx, cpIdx);
    });
}

function setSlideMode(mode) {
    if(mode === '0') {//非滑动
        $('.coursePanel').css('fontSize', '0.77rem');
        $('td').removeClass('slideMode');
        $('#tableDiv').removeClass('slideMode');
        setCpHeight();
    } else if(mode === '1'){//滑动
        $('.coursePanel').css('fontSize', '1.00rem');
        $('td').addClass('slideMode');
        $('#tableDiv').addClass('slideMode');
        setCpHeight();
    }
}

function initPopover() {
    $('.coursePanel').unbind().popover({
        placement: 'right',
        html: true,
        container: 'body',
        trigger: ' manual' //手动触发
    }).on('show.bs.popover', function () {
        $(this).addClass('popover_open');
    }).on('hide.bs.popover', function () {
        $(this).removeClass('popover_open');
    }).click(function (ev) {
        if (customMode)
            return;
        if ($(this).hasClass('popover_open')) {
            $(this).popover('hide')
        } else {
            $('.popover_open').popover('hide');
            $(this).popover('show');
        }
        stopEventPropagation(ev);
    });
    $(document).click(function () {
        $('.coursePanel').popover('hide');
    });
}

function stopEventPropagation(ev) {
    const e = ev || window.event || arguments.callee.caller.arguments[0];
    e.stopPropagation();
}


$(document).ready(function () {
    recordOriCourseTable();
    readCourse();
    setCourseContent();
    initPopover();
    $('#main').show();
    setSlideMode(slideMode);
    $('#customButton').show();
    $('#updateButton').show();
    $('#customButton').click(function () {
        if (customMode) {//正在自定义模式中，则退出自定义模式
            customMode = false;
            $('.cpTd').unbind();
            initPopover();
            $(this).text('自定义课表');
            $('.addIcon').remove();
            $('.editIcon').remove();
            $('#resetButton').hide();
            setCpHeight();
            saveModifiedTimeTable();
        } else {//进入自定义模式
            customMode = true;
            initCustomMode();
            $(this).text('完成自定义');
            $('.addIcon').css('display', 'block');
            $('.editIcon').css('display', 'block');
            $('#resetButton').show();
            setCpHeight();
            $('#resetButton').click(function () {
                $('#confirmContent').text('确认恢复默认课表？所有更改将不会被保存。');
                $('#confirmModal').modal('show');
                $('#confirmBtn').click(function () {
                    resetCourse();
                });
            });
        }
    });

    $('#slideModeButton').html(slideMode === '0' ? '切换滑动模式' : '关闭滑动模式')
        .click(function () {
            slideMode = slideMode === '0' ? '1' : '0';
            setSlideMode(slideMode);
            localStorage.setItem('ymslidemode', slideMode);
            $(this).html(slideMode === '0' ? '切换滑动模式' : '关闭滑动模式');
        });

    $('#updateButton').click(function () {
        showLoading();
        $(this).attr('disabled', 'disabled');
        window.location.href = getNewTimeTableUrl();
    });

    $('#delCourseBtn').click(function () {
        $('#confirmModal').modal('show');
    });

    $('#tableOuterDiv')[0].scrollTo($('#tableOuterDiv')[0].scrollWidth * (thisWeekDay - 1) / 7, 0);

});


const localMap = new Map();
const oriCourseSet = new Set();
let localJson;
let thisWeekDay = new Date().getDay() - 1;
thisWeekDay = thisWeekDay === -1 ? 6 : thisWeekDay;
let customMode = false;