'use strict';

var llrp = require('./node_modules/llrp/LLRPMain.js');
var express = require("express");
var app = express();
var port = 3700;
var assets = [0,1,2];
var reader = new llrp({
    ipaddress: '192.168.1.50',
    isReaderConfigSet: false,
    log: true,
    isStartROSpecSent : false
});

app.set('views', __dirname + '/views');
app.set('view engine', "jade");
app.engine('jade', require('jade').__express);
app.get("/", function(req, res){
    res.render("main_page",{numSigns: assets});
});
app.use(express.static(__dirname + '/public'));

var io = require('socket.io').listen(app.listen(port));
io.sockets.on('connection', function (socket) {
    //socket.emit('message', { message: 'welcome to the chat' });
    console.log("message sent to the window")
    socket.on('start', function (data) {
        reader.connect();
        socket.emit('message',data);
    });
    socket.on('stop', function (data) {
        reader.disconnect();
        socket.emit('message',data);
    });
    socket.on('save', function (data) {
        socket.emit('message',data);
    });
    socket.on('layout', function (data) {
        if(3==assets.length)
            assets=[1];
        else if(2==assets.length)
            assets=[0,1,2];
        else if(1==assets.length)
            assets=[0,1];
        console.log("asset size="+assets.length);
        socket.emit('message',data);
    });
    socket.on('exit', function (data) {
        socket.emit('message',data);
    });
});

reader.on('timeout', function () {
    console.log('timeout');
});

reader.on('disconnect', function () {
    console.log('disconnect');
});

reader.on('error', function (error) {
    console.log('error: ' + JSON.stringify(error));
});

reader.on('didSeeTag', function (tag) {
    console.log('TAG: ' + tag.tagID);
});

console.log("Listening on port " + port);
