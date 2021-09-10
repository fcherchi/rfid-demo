


var stompClient = {
    client: null,
    socket: null,
    output: null,
    connect: function (output) {
        this.socket = new SockJS('/console');
        this.client = Stomp.over(this.socket);
        this.output = output;
        this.client.connect({}, function (frame) {
            stompClient.client.subscribe('/topic', function (events) {
                stompClient.consume(events);
            });
        });
    },
    consume: function (raw) {
        writeToScreen(raw, output)
    },
    close: function () {
        if (this.client != null && this.client != undefined) {
            this.client.unsubscribe('/topic');
            this.client.disconnect();
            this.client = null;
        }
    }
};





function cableOff(url) {
    $.getJSON(url)
        .done(function (result) {

        });
}

function writeToScreen(message, output)
{
    // console.log(output)
    var pre = document.createElement("p");
    pre.style.wordWrap = "break-word";
    pre.innerHTML = message.body;
    output.appendChild(pre);
    output.parentNode.scrollTop = output.parentNode.scrollHeight;
}

function createTrReader(index, reader){

    var html = '';
    html += '<tr>';
    html += '<td class="col-sm-4">' + reader.readerId +'</td>';
    html += '<td class="col-sm-4">' + reader.enabled + '</td>';
    html += '<td><button data-action="cable_off" data-value="'+ reader.readerId + '" ' +
        'class="btn btn-default"><i class="fa fa-times"></i>Pull Cable</button></td>';
    html += '</tr>';

    return html;
}
function createTrHeartbeat(index, heartbeat){

    var html = '';
    var date = new Date(heartbeat.lastTimestampAlive.epochSecond * 1000);


    html += '<tr">';
    html += '<td class="col-sm-3">' + index + '</td>';
    if (heartbeat.alive) {
        html += '<td class="col-sm-3 bg-success">' + heartbeat.alive + '</td>';
    } else {
        html += '<td class="col-sm-3 bg-danger">' + heartbeat.alive + '</td>';
    }

    html += '<td class="col-sm-3">' + date.toISOString() +'</td>';
    html += '<td class="col-sm-3">' + heartbeat.temperature +'</td>';
    html += '</tr>';

    return html;
}


