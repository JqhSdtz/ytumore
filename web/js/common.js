let env = '';

init();//执行初始化

function init() {
    if(typeof fromApp !== 'undefined' && fromApp)
        env = 'app';
    else
        env = getParamOfUrl('env');
    initMpScript();
    initStorage();
    updateStorage();
    setDebug();
    //getEnv();
}

function initMpScript() {
    if(env === 'wxmp' || env === 'wxgzh')
        getScriptSync('https://res.wx.qq.com/open/js/jweixin-1.3.2.js');
    else if(env === 'qqmp')
        getScriptSync('https://qqq.gtimg.cn/miniprogram/webview_jssdk/qqjssdk-1.0.0.js');
}

function getParamOfUrl(name, href) {
    let query;
    if(typeof href !== 'undefined')
        query = href.split('?').length > 1 ? href.split('?')[1] : '';
    else
        query = window.location.search.substring(1);
    const vars = query.split('&');
    for (let i = 0; i < vars.length; i++) {
        let pair = vars[i].split('=');
        if (pair[0] == name) {
            return pair[1];
        }
    }
    return '';
}

/**操作Cookie基本方法*/
function setCookie(cname, cvalue, exdays) {
    const d = new Date();
    d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
    const expires = 'expires=' + d.toGMTString();
    document.cookie = cname + '=' + cvalue + '; ' + expires;
}

function getCookie(cname) {
    const name = cname + '=';
    const ca = document.cookie.split(';');
    for (let i = 0; i < ca.length; i++) {
        const c = ca[i].trim();
        if (c.indexOf(name) === 0) return c.substring(name.length, c.length);
    }
    return '';
}

/**获取各部分连接的方法，目的是实现根据页面名称实现页面缓存和页面缓存更新*/
function getUrl(base, hasArg, pgName, pgCdName, withDfCd, withUid) {
    let url = contextPath + '/' + base;
    url += (hasArg ?  '&env=' : '?env=') + env;
    if(withDfCd)
        url += '&dfcd=' + localStorage.getItem('ymlgdiffcode');
    if(withUid)
        url += '&uid=' + localStorage.getItem('ymuid');
    if(pgName !== null)
        url += '&v=' + fileSeqMap.get(pgName);
    if(pgCdName !== null) {
        if(isNaN(parseInt(pgCdName)))//传进来的是名字
            url += '&pgcd=' + localStorage.getItem(pgCdName);
        else//传进来的是整型字符串
            url += '&pgcd=' + pgCdName;
    }
    return url;
}

function getTimeTableUrl() {
    return getUrl('timeTable.ym', false, 'timeTable.jsp',
        'ymttcode', true, true);
}

function getTermGradeUrl() {
    return getUrl('grade.ym?type=term', true, 'termGrade.jsp',
        'ymtermgdcode', true, true);
}

function getAllGradeUrl() {
    return getUrl('grade.ym?type=all', true, 'allGrade.jsp',
        'ymallgdcode', true, true);
}

function getFailGradeUrl() {
    return getUrl('grade.ym?type=failed', true, 'failedGrade.jsp',
        'ymfailedgdcode', true, true);
}

function getExamScheduleUrl() {
    return getUrl('exam.ym', false, 'examSchedule.jsp',
        'ymexcode', true, true);
}

function getClassTimeTableQueryUrl() {
    return getUrl('classTimeTable.ym?query=1', true, 'classTimeTableQuery.jsp',
        null, false, false);
}

function getRoomQueryUrl() {
    return getUrl('room.ym?query=1', true, 'roomQuery.jsp',
        null, false, false);
}

function getTeacherInfoQueryUrl() {
    return getUrl('teacherInfo.ym?query=1', true, 'teacherInfoQuery.jsp',
        null, false, false);
}

function getIndexUrl() {
    return getUrl('index.jsp', false, 'index.jsp',
        null, false, false);
}

function getNewGradeUrl(type) {
    let randSeq = Math.floor(Math.random() * 1000000).toString();
    localStorage.setItem('ym' + type + 'gdcode', randSeq);
    return getUrl('newGrade.ym?type=' + type, true, type + 'Grade.jsp',
        randSeq, true, true);
}

function getNewTimeTableUrl() {
    let randSeq = Math.floor(Math.random() * 1000000).toString();
    localStorage.setItem('ymttcode', randSeq);
    return getUrl('newTimeTable.ym', false, 'timeTable.jsp',
        randSeq, true, true);
}

function getNewExamUrl() {
    let randSeq = Math.floor(Math.random() * 1000000).toString();
    localStorage.setItem('ymexcode', randSeq);
    return getUrl('newExam.ym', false, 'examSchedule.jsp',
        randSeq, true, true);
}

const noticePageNames = ['sysBusy.jsp', 'ttUnAvl.jsp', 'unconnected.jsp', 'unprepared.jsp', 'updateWarning.jsp'];
function getNoticeUrl(ref, type) {
    ref = parseInt(ref);
    if(ref === -6 || ref === -7) ref = 2;
    const pageName = noticePageNames[ref];
    return getUrl('pages/notice/' + pageName + '?type=' + type, true, pageName,
        null, false, false);
}

/**若本页面来自登录页面，则将用户数据保存到本地*/
function initStorage() {
    if (typeof clientUid === 'undefined')
        return;
    if (localStorage.getItem('ymfromlogin') === '1') {
        if (clientUid === '') {//来自登录页面，但用户ID为空
            // 此时为异常情况，跳转回登录界面
            window.location.href = contextPath + '/login.ym?show=1&env=' + env;
        } else {
            localStorage.setItem('ymuid', clientUid);
            localStorage.setItem('ympwd', '2' + cookiePwd);
            localStorage.setItem('ymlcode', cookieLCode);
            setCookie('ymuid', clientUid, 30);
            setCookie('ympwd', '2' + cookiePwd, 30);
        }
        localStorage.setItem('ymfromlogin', '0');
    }
}

function updateStorage() {

    /**将cookie转存到localStorage，并验证时间戳*/
    const cTStamp = getCookie('ymtstamp');
    if (cTStamp !== '' && localStorage.getItem('ymtstamp') !== cTStamp) {
        //如果localStorage的ymtstamp等于cTStamp，则说明这个时间已经被戳过了
        //说明该cookie已经被转存到了localStorage，那么就不继续操作
        const cPwd = getCookie('ympwd');
        const cUid = getCookie('ymuid');
        if (new Date().getTime() - parseInt(cTStamp) < 10000) {
            if (cPwd !== '')
                localStorage.setItem('ympwd', cPwd);
            if (cUid !== '')
                localStorage.setItem('ymuid', cUid);
        }
        setCookie('ymtstamp', '', 0);
        localStorage.setItem('ymtstamp', cTStamp);
    }

    /**若cookie和localStorage不一致，则更新cookie*/
    if (localStorage.getItem('ympwd') !== getCookie('ympwd')
        && localStorage.getItem('ympwd') !== null) {
        //确保cookie与localStorage一致，因为在APP上页面不会重新加载，cookie不会改变
        // 而且cookie还会过期，所以要在这里手动设定一下
        setCookie('ymuid', localStorage.getItem('ymuid'), 30);
        setCookie('ympwd', localStorage.getItem('ympwd'), 30);
    }
}

function setBackFun(fun) {//设置浏览器返回上一页时的操作
    window.history.pushState({
        title: 'title',
        url: '#'
    }, null, '#');
    window.addEventListener('popstate', function() {
        fun();
    }, false);
}

function clearStorage() {
    localStorage.removeItem('ympwd');
    localStorage.removeItem('ymuid');
    localStorage.removeItem('ymlcode');
    setCookie('ymuid', '', 0);
    setCookie('ympwd', '', 0);
}

/**设置debug状态*/
function setDebug() {
    if(typeof clientUid !== 'undefined') {
        if(debug)
            localStorage.setItem('ymdebug', '1');
        else
            localStorage.removeItem('ymdebug');
    }
}

function showLoading() {
    if(env !== 'app') {
        const html = '<div class="sk-chase">' +
            '<div class="sk-chase-dot"></div>' +
            '<div class="sk-chase-dot"></div>' +
            '<div class="sk-chase-dot"></div>' +
            '<div class="sk-chase-dot"></div>' +
            '<div class="sk-chase-dot"></div>' +
            '<div class="sk-chase-dot"></div>' +
            '</div>';
        $("#loadingDiv").append($(html)).show();
    }
}

/**debug模式下运行的代码*/
// if (debug) {
//     const msg = 'cookie\n' + 'ymuid=' + getCookie('ymuid')
//         + '\nympwd=' + getCookie('ympwd')
//         + '\nymtstamp=' + getCookie('ymtstamp')
//         + '\nlocalStorage\n' + 'ymuid=' + localStorage.getItem('ymuid')
//         + '\nympwd=' + localStorage.getItem('ympwd');
//     alert(msg);
// }