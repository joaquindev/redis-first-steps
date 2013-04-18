package controllers;

import play.*;
import play.mvc.*;

import views.html.*;
import models.RMessage;

public class Application extends Controller {
  
    public static Result index() {
        Long count = RMessage.count();
        return ok(index.render(count));
    }

    public static Result wall() {
        return ok(wall.render());
    }
  
}
