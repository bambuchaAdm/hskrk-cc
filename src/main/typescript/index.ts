function component(){
    'use strict';

    var element= document.createElement('div');

    element.innerHTML = "Hellow webpack";

    return element;
}

document.body.appendChild(component());