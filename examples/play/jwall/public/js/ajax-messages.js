$(document).ready(function(){     
   $(window).bind('scroll', loadOnScroll);
});

// Scroll globals
var pageNum = 0; // The latest page loaded
var hasNextPage = true; // Indicates whether to expect another page after this one

// loadOnScroll handler
var loadOnScroll = function() {
   // If the current scroll position is past out cutoff point...
    if ($(window).scrollTop() > $(document).height() - ($(window).height()*2)) {
        // temporarily unhook the scroll event watcher so we don't call a bunch of times in a row
        $(window).unbind(); 
        // execute the load function below that will visit the JSON feed and stuff data into the HTML
        loadItems();
    }
};

var loadItems = function() {
    // If the next page doesn't exist, just quit now 
    if (hasNextPage === false) {
        return false
    }
    
    // Update the page number
    pageNum = pageNum + 1;
    
    // Configure the url we're about to hit
    $.ajax({
        url: 'get-messages',
        data: {page: pageNum},
        dataType: 'json',
        success: function(data) {
            messages_div = $("#messages");
            for (i=0;i < data.length; i++)
            {
                msg = data[i];
                message = msg['message'];
                by = msg['by'];
                to = msg['to'];
                date = msg['date'];
                
                rowFluid = $('<div class="row-fluid"></div>');
                message_div = $('<div class="span12 well"></div>');
                rowFluid.append(message_div);
                messages_div.append(rowFluid);
                messages_div.append(rowFluid);

                message_div.append("<h4>To <strong>" + to + "</strong></h4>")
                message_div.append("<div>" + message + "</div>")
                message_div.append("<div>by <strong>" + by + "</strong> on " + date + "</div>")

            }
        },
        error: function(data) {
            hasNextPage = false
        },
        complete: function(data, textStatus){
            // Turn the scroll monitor back on
            $(window).bind('scroll', loadOnScroll);
        }
    });
};