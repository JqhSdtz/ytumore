function process() {
    $('#submitButton').attr("disabled", "disabled");
    const lgPwd = '0' + new Date().getTime() + $('#pwd').val();
    $('#pwd').val(aes_encrypt($('#aesKey').val(), lgPwd));
    localStorage.setItem('ymhaslogin', '1');
    localStorage.setItem('ymfromlogin', '1');
}

function aes_encrypt(key, data) {
    key = CryptoJS.enc.Utf8.parse(key);
    const res = CryptoJS.AES.encrypt(data, key, {
        mode: CryptoJS.mode.ECB,
        padding: CryptoJS.pad.Pkcs7
    }).toString();
    return res;
}

if(logout) {
    const oriUrl = $('#oriUrl').val();
    if(typeof oriUrl !== 'undefined' && oriUrl !== null && oriUrl !== '') {
        $('#oriUrl').val(unescape(oriUrl));
    }
}
if(env === '')
    env = getParamOfUrl('env', $('#oriUrl').val());
$('#loginForm').attr('action', $('#loginForm').attr('action') + env);
if(env === 'wxmp' || env === 'qqmp' || env === 'wxgzh') {//小程序环境下保障不会跳到主页
    const oriUrl = $('#oriUrl').val();
    if(typeof oriUrl === 'undefined' || oriUrl === null || oriUrl === '') {//oriUrl为空
        const href = window.location.href;
        if(href.includes('/login.ym') || href.includes('/logout.ym'))//当前路径是登录或注销
            $('#oriUrl').val(getTimeTableUrl());//设之前路径为课表(不是办法的办法)
        else
            $('#oriUrl').val(href);//当前路径不是登陆或注销，直接把当前路径设为oriUrl
    } else if(oriUrl === 'login')//oriUrl是登录或注销，则改为课表
        $('#oriUrl').val(getTimeTableUrl());
}
if(!login)
    localStorage.setItem('ymhaslogin', '0');
setBackFun(function () {
    if (env === 'wxmp' || env === 'wxgzh') {
        wx.miniProgram.navigateBack({
            delta: 5
        });
    } else if (env === 'qqmp') {
        qq.miniProgram.navigateBack({
            delta: 5
        });
    } else
        window.location.href =  getIndexUrl();
});