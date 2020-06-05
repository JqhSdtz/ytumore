const isMobile = window.screen.availWidth < 1000;
const isInMp = env === 'wxmp' || env === 'qqmp';
const isInGzh = env === 'wxgzh';

const topBar = $('<div></div>');
const topBarMoreOptDiv = $('<div></div>');
const topBarTitle = $('<p></p>')
const topBarBackIcon = $('<span class="icon-angle-left"></span>');
const topBarBackLan = $('<span>' + ((isInMp || isInGzh) ? '注销' : '返回') + '</span>');
const topBarBackDiv = $('<div></div>');
const topBarMoreDiv = $('<div style="display: none"></div>');
const topBarMoreLan = $('<span></span>');
const topBarMoreIcon = $('<span class="icon-angle-down"></span>');
let backHref = '_blank';
let offset = 0;
const topBarHeight = 3;
if (typeof statusBar !== 'undefined')
    offset = statusBarHeight;
topBar.css({
    position: 'fixed',
    width: '100%',
    top: offset.toString() + 'rem',
    height: topBarHeight.toString() + 'rem',
    backgroundColor: '#233333',
    zIndex: '10'
});
topBarMoreOptDiv.css({
    position: 'fixed',
    top: (offset + topBarHeight).toString() + 'rem',
    backgroundColor: '#233333',
    width: '100%',
    zIndex: '10',
    display: 'none'
});
topBarBackIcon.css({
    display: (isInMp || isInGzh) ? 'none' : 'inline',
    position: 'absolute',
    left: '5%',
    top: isMobile ? '0.19rem' : '0.23rem',
    color: 'white',
    fontSize: '2.5rem'
});
topBarBackLan.css({
    position: 'absolute',
    left: isMobile ? '10%' : '9%',
    top: isMobile ? '0.57rem' : '0.35rem',
    color: 'white',
    fontSize: isMobile ? '1.25rem' : '1.5rem'
});
topBarTitle.css({
    textAlign: 'center',
    color: 'white',
    fontSize: '1.75rem',
    marginTop: '0.15rem'
});
topBarMoreLan.css({
    position: 'absolute',
    right: isMobile ? '12%' : '11%',
    top: isMobile ? '0.57rem' : '0.35rem',
    color: 'white',
    fontSize: isMobile ? '1.25rem' : '1.5rem'
});
topBarMoreIcon.css({
    position: 'absolute',
    right: '6%',
    top: '0.5rem',
    color: 'white',
    fontSize: '2rem'
});

let isOptDivShown = false;
let hasMoreOpt = false;

function setTopBar(option) {
    backHref = option.backHref === '' ? '/' : option.backHref;
    if (isInMp || isInGzh) {
        topBarBackDiv.click(function () {
            window.location.href = getUrl('logout.ym?logout=1&oriUrl='
                + escape(window.location.href + '&lgots=' + new Date().getTime()),
                true, null, null, true, true);
        });
        setBackFun(function () {
            if (backHref.includes('index.jsp')) {
                if (env === 'wxmp' || env === 'wxgzh') {
                    wx.miniProgram.navigateBack({
                        delta: 5
                    });
                } else if (env === 'qqmp') {
                    qq.miniProgram.navigateBack({
                        delta: 5
                    });
                }
            } else {
                window.location.href = backHref;
            }
        });
    } else {
        function fun() {
            window.location.href = backHref;
        }

        topBarBackDiv.click(fun);
        //setBackFun(fun);
    }
    topBarTitle.text(option.title);
    if (typeof option.moreOpt !== 'undefined') {
        topBarMoreDiv.css('display', 'block');
        hasMoreOpt = true;
    } else
        return;
    topBarMoreLan.text(option.moreLan);
    for (let i = 0; i < option.moreOpt.length; ++i) {
        const opt = option.moreOpt[i];
        const optElem = $('<div></div>');
        optElem.css({
            width: '100%',
            height: topBarHeight.toString() + 'rem',
            borderTop: 'solid 0.03rem rgba(0,0,0,0.8)'
        }).append($('<p></p>').css({
            textAlign: 'center',
            color: 'white',
            fontSize: '1.75rem',
            marginTop: '0.15rem'
        }).text(opt.title)).click(function () {
            window.location.href = opt.url;
        });
        topBarMoreOptDiv.append(optElem);
    }

    topBarMoreDiv.click(function () {
        topBarMoreOptDiv.toggle();
        if (isOptDivShown) {
            topBarMoreIcon.removeClass('icon-angle-up');
            topBarMoreIcon.addClass('icon-angle-down');
            isOptDivShown = false;
        } else {
            topBarMoreIcon.removeClass('icon-angle-down');
            topBarMoreIcon.addClass('icon-angle-up');
            isOptDivShown = true;
        }
    });
}

topBar.append(topBarTitle);
topBar.append(topBarMoreOptDiv);
topBar.append(topBarMoreDiv);
topBar.append(topBarBackDiv);
topBarBackDiv.append(topBarBackIcon);
topBarBackDiv.append(topBarBackLan);
topBarMoreDiv.append(topBarMoreIcon);
topBarMoreDiv.append(topBarMoreLan);

$(document.body).append(topBar);
