const pageContext = $('#pageContext').val();
const submitButton = $('#submitButton');
submitButton.click(function () {
    const key = $('#pwd').val();
    const cmd = $('#cmd').val() + "&timeStamp=" + new Date().getTime()
        + "&managerId=" + $('#managerId').val();
    const data = encrypt(key, cmd);
    $('#data').val(data);
    submitCmd();
});

function submitCmd() {
    $.ajax({
        type: "POST",
        url: pageContext + "/manage.ym",
        data: {
            set: 1,
            data: $('#data').val(),
            managerId: $('#managerId').val()
        },
        beforeSend: function () {
            submitButton.attr("disabled", "disabled");
        },
        success: function (data) {
            const json = JSON.parse(decrypt($('#pwd').val(), data));
            if(json.hasOwnProperty("msg")){
                $('#result').val(json.msg);
                $('#managerId').val(json.manageId);
            }else{
                $('#result').val(json.error);
            }
        },
        complete: function () {
            submitButton.removeAttr("disabled");
        }
    });
}

function encrypt(key, data) {
    key = CryptoJS.enc.Utf8.parse(key);
    const res = CryptoJS.AES.encrypt(data, key, {
        mode: CryptoJS.mode.ECB,
        padding: CryptoJS.pad.Pkcs7
    }).toString();
    return res;
}

function decrypt(key, data) {
    key = CryptoJS.enc.Utf8.parse(key);
    let res = CryptoJS.AES.decrypt(data, key, {
        mode: CryptoJS.mode.ECB,
        padding: CryptoJS.pad.Pkcs7
    });
    res = CryptoJS.enc.Utf8.stringify(res);
    return res;
}

$('#cmd').keydown(function (event) {
    if (event.keyCode === 13) {
        $(this).blur();
        submitButton.click();
    }
});