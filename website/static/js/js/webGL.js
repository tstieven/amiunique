function getWebGL(){
  canvasJs = document.createElement('canvas');
  var ctx = canvas.getContext("webgl") || canvas.getContext("experimental-webgl");
  if(ctx.getSupportedExtensions().indexOf("WEBGL_debug_renderer_info") >= 0) {
      webGLVendor = ctx.getParameter(ctx.getExtension('WEBGL_debug_renderer_info').UNMASKED_VENDOR_WEBGL);
      webGLRenderer = ctx.getParameter(ctx.getExtension('WEBGL_debug_renderer_info').UNMASKED_RENDERER_WEBGL);
  } else {
      webGLVendor = "Not supported";
      webGLRenderer = "Not supported";
  }
  return [webGLVendor, webGLRenderer].join(";;;");
}

fp.webGLInfo = getWebGL();