const pRem = 16;
const pcWidth = 1024, phWidth = 414;
const devWidthLim = 1000;

setRem();
window.onload = function () {
	if (typeof setFooter === 'function')
		setFooter();
};
window.onresize = function () {
	setRem();
	if (typeof setFooter === 'function')
		setFooter();
};

function setRem() {
	let html = document.getElementsByTagName('html')[0];
	let oriWidth = document.body.clientWidth || document.documentElement.clientWidth;
	let width;
	if (oriWidth > devWidthLim)
		width = pcWidth;
	else
		width = phWidth;
	html.style.fontSize = oriWidth / width * pRem + 'px';
}

function setScaledRem(times){
	let oriWidth = document.body.clientWidth || document.documentElement.clientWidth;
	let width;
	if (oriWidth > devWidthLim)
		width = pcWidth;
	else
		width = phWidth;
	return oriWidth / width * pRem * times + 'px';
}

function getRem() {
	return document.getElementsByTagName('html')[0].style.fontSize;
}