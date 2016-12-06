cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
    {
        "file": "plugins/org.apache.cordova.webview/www/WebViewRequest.js",
        "id": "com.example.yangxiaoguang.cordovademo.Cordova.Plugin.WebViewRequest",
        "merges": [
        "cdv.webview"
        ]
    },
     {
        "file": "plugins/org.apache.cordova.ajax/www/AjaxRequest.js",
        "id": "com.suypower.stereo.suehome.CordovaPlugin.ajax.AjaxRequest",
        "merges": [
        "ajax"
        ]
     },

];
module.exports.metadata = 
// TOP OF METADATA
{
    "com.suypower.stereo.suehome.CordovaPlugin.http.FavoriteRequest": "0.0.1",


}
// BOTTOM OF METADATA
});