import "pure-css";

function component(){
    'use strict';

    const element= document.createElement('div');

    element.innerHTML = "Hi webpack";

    return element;
}

document.body.appendChild(component());