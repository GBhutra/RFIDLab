var express = require("express");
var app = express();
var port = 3700;
var numSigns = 3;

app.set('views', __dirname + '/views');
app.set('view engine', "jade");
app.engine('jade', require('jade').__express);
app.get("/", function(req, res){
    res.render("main_page");
});
app.use(express.static(__dirname + '/public'));

var io = require('socket.io').listen(app.listen(port));
io.sockets.on('connection', function (socket) {
    //socket.emit('message', { message: 'welcome to the chat' });
    console.log("message sent to the window")
    socket.on('start', function (data) {
        socket.emit('message',data);
    });
    socket.on('stop', function (data) {
        socket.emit('message',data);
    });
    socket.on('save', function (data) {
        socket.emit('message',data);
    });
    socket.on('layout', function (data) {
        socket.emit('message',data);
    });
    socket.on('exit', function (data) {
        socket.emit('message',data);
    });
});

console.log("Listening on port " + port);
