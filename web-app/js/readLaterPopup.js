/**
 * 
 */

javascript: q = location.href;
if (document.getSelection) {
	d = document.getSelection();
} else {
	d = '';
};
p = document.title;
void (open('http://localhost:8080/readLater/add?url=' + encodeURIComponent(q)
		+ '&description=' + encodeURIComponent(d) + '&title='
		+ encodeURIComponent(p), 'MorningMail', 'toolbar=no,width=400,height=100'));