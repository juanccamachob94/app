function demoFromHTML() {
    var pdf = new jsPDF('p', 'pt', 'letter');
    // source can be HTML-formatted string, or a reference
    // to an actual DOM element from which the text will be scraped.
    source = $('.diagrama')[0];

    // we support special element handlers. Register them with jQuery-style 
    // ID selector for either ID or node name. ("#iAmID", "div", "span" etc.)
    // There is no support for any other type of selectors 
    // (class, of compound) at this time.
    specialElementHandlers = {
        // element with id of "bypass" - jQuery style selector
        '#bypassme': function (element, renderer) {
            // true = "handled elsewhere, bypass text extraction"
            return true
        }
    };
    margins = {
        top: 80,
        bottom: 60,
        left: 40,
        width: 522
    };
    // all coords and widths are in jsPDF instance's declared units
    // 'inches' in this case
    pdf.fromHTML(
    source, // HTML string or DOM elem ref.
    margins.left, // x coord
    margins.top, { // y coord
        'width': margins.width, // max width of content on PDF
        'elementHandlers': specialElementHandlers
    },

    function (dispose) {
        // dispose: object with X, Y of the last line add to the PDF 
        //          this allow the insertion of new lines after html
        pdf.save('Test.pdf');
    }, margins);
}



/**
 * Para mostrar la ventana de cargando (la animación que aparece al iniciar sesión en el sistema)
 */
function mostrarCargando() {
    window['progress'] = setInterval(function() {
        var pbClient = PF('msgCargando'),
        oldValue = pbClient.getValue(),
        newValue = oldValue + 10;
        pbClient.setValue(pbClient.getValue() + 10);
        if(newValue === 100) clearInterval(window['progress']);
    }, 300);
    for (var i = 0; i < arguments.length; i++) PF(arguments[i]).show();
}

/**
 * Para ocultar la ventana de cargando (la animación que aparece al iniciar sesión en el sistema)
 */
function ocultarCargando() {
	for (var i = 0; i < arguments.length; i++) PF(arguments[i]).hide();
    clearInterval(window['progress']);
    PF('msgCargando').setValue(0);
}

function posicionarFijo() {
	$('body').css('position','fixed');
	try {
		setTimeout(function() {
			$('.ui-message-dialog').css('top', $('#primefacesmessagedlg_modal').height()/2-parseInt(($('.ui-message-dialog').css('height')).replace('px',''))/2 + 'px');
			$('.ui-message-dialog').css('left', $('#primefacesmessagedlg_modal').width()/2-parseInt(($('.ui-message-dialog').css('width')).replace('px',''))/2 + 'px');
		},10);
	} catch(ex){}
}


/**
 * Para mostrar un item de Primefaces-JSF
 * Se adicionó el cambio de posición del cuerpo del documento
 * para que el modal se adapte adecuadamente a la pantalla
 * y no al tamaño real del contenido de la página, lo que estaba
 * provocando que se posicionara más abajo de lo que debía
 */
function mostrar(p) {
	posicionarFijo();
	PF(p).show();
	$('body').css('position','inherit');
}

/**
 *Para ocultar un item de Primefaces-JSF
 */
function ocultar(p) {
	PF(p).hide();
}

$( document ).ready(function() {
	$('#imagenPerfil').height($('#imagenPerfil').width());
});


$(window).resize(function() {
	$('#imagenPerfil').height($('#imagenPerfil').width());
	});