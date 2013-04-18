package controllers;

import play.*;
import play.mvc.*;
import views.html.*;
import play.data.DynamicForm;
import play.data.Form;

import java.util.List;

import models.RMessage;

public class Application extends Controller {
  
    public static Result index() {
        Long count = RMessage.count();
        return ok(index.render(count));
    }

    public static Result wall() {
        List<RMessage> messages = RMessage.getAll();
        return ok(wall.render(messages));
    }

    public static Result saveMessage(){
        DynamicForm requestData = Form.form().bindFromRequest();
        String message = requestData.get("message");
        String by = requestData.get("by");
        String to = requestData.get("to");

        RMessage m = new RMessage(message, by, to);
        m.save();
        return  redirect("/wall");

    }
  
}
