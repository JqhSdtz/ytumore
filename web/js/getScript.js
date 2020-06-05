//必须放在jQuery后！！
function getScriptWithSeq(fileName, async) {
    const seq = fileSeqMap.get(fileName);
    if(typeof seq === 'undefined') {
        console.log(fileName + ' not found!');
        return;
    }
    if(async)
        $.getScript(staticFilePath + '/js/' + fileName + '?v=' + seq);
    else
        getScriptSync(staticFilePath + '/js/' + fileName + '?v=' + seq);
}

function getScriptSync(url) {
    $.ajaxSettings.async = false;
    $.getScript(url);
    $.ajaxSettings.async = true;
}
$.ajaxSetup({
    cache: true
});