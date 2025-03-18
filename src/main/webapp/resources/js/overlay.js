/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/javascript.js to edit this template
 */


function setOverlayBackground(imageUrl) {
    // Wait briefly to ensure the overlay is rendered (PrimeFaces modal rendering timing)
    setTimeout(function() {
        var overlay = $('.ui-widget-overlay');
        if (overlay.length) {
            overlay.css({
                'background': "rgba(0,0,0,0.5) url('" + imageUrl + "') no-repeat center center / cover"
            });
        } else {
            console.warn('No overlay found for movieDialog');
        }
    }, 50); // Small delay to ensure overlay exists
}