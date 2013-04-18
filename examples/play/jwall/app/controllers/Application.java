package controllers;

import play.*;
import play.mvc.*;
import views.html.*;

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
  
}
