    let tipContentStr = "之前没图标的APP是个意外" +
        "<a href='https://www.ytumore.cn/ym.apk?version=2'>点击下载有图标的APP</a>";
    // if (clientUid !== '' && clientUid.substring(0, 4) === '2019')
    //     tipContentStr += '</br>19级的小伙伴开学快乐呀~';
    /**通常显示的内容*/
    const usualContactMeStr = "网站使用过程中出现的任何问题或者您对本网站有任何意见或建议," +
        "请发邮件至jqhsdtz@foxmail.com</br>感谢您的使用"

    const newTipSeq = "6";
    const expireDate = new Date('2019-09-30 00:00:00');

    let isTipWordsShown = false;
    if (showCondition()) {
        isTipWordsShown = true;
        //ymtipwordstoggle = 0 表示关闭消息通知
        $('.tipWords').addClass("text-danger");
        $('#tipWord1').text("[Hey]");
        $('#tipWord2').text("[Look]");
        $('#tipWord3').text("[here]");
        $('#tipContent').html(tipContentStr);
        $('#contactMeBtn').click(function () {
            $('.tipWords').html("&nbsp;&nbsp;");
            $('.tipWords').removeClass("text-danger");
        });
        // $('#contactMeModal').on('hidden.bs.modal', function () {
        //     $('#tipContent').html(usualContactMeStr);
        // });
    } else {
        $('#tipContent').html(usualContactMeStr);
    }

    if (isTipWordsShown) {
        showTipWordsHasReadBtn();
    }

    if (isTipWordsShown && localStorage.getItem("ymtipwordstoggle") != "0") {
        $('#tipWordsToggleBtn').show().html("不再接收通知").click(closeTipWords);
    } else if (localStorage.getItem("ymhaslogin") === '1'
        && localStorage.getItem("ymtipwordstoggle") == "0") {
        $('#tipWordsToggleBtn').show().html("开启通知").click(openTipWords);
    }

    function showCondition() {
        if(localStorage.getItem('ymhaslogin') !== '1')
            return false;
        if(localStorage.getItem("ymtiphasread" + newTipSeq) !== null)
            return false;
        if(hasDateExpired(expireDate))
            return false;
        if(localStorage.getItem("ymtipwordstoggle") === '0')
            return false;
        if(localStorage.getItem('ymappversion') !== '1')
            return false;
        return true;
    }

    function openTipWords() {
        localStorage.setItem("ymtipwordstoggle", "1");
        $('#tipWordsToggleBtn').html("不再接收通知").click(closeTipWords);
        $('#tipContent').html(tipContentStr);
        showTipWordsHasReadBtn();
    }

    function showTipWordsHasReadBtn() {
        $('#tipWordsHasReadBtn').show().click(function () {
            $('#tipContent').html(usualContactMeStr);
            $(this).hide();
            $('#tipWordsToggleBtn').hide();
            localStorage.setItem("ymtiphasread" + newTipSeq, "1");
        });
    }

    function closeTipWords() {
        localStorage.setItem("ymtipwordstoggle", "0");
        $('#tipWordsToggleBtn').html("开启通知").click(openTipWords);
    }

    function hasDateExpired(expireDate) {
        if (new Date().getTime() > expireDate.getTime())
            return true;
        return false;
    }