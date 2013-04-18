package controllers;

import play.*;
import play.mvc.*;
import views.html.*;
import play.data.DynamicForm;
import play.data.Form;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import models.RMessage;
import utils.DateUtils;

public class Application extends Controller {
  
    public static Result index() {
        Long count = RMessage.count();
        return ok(index.render(count));
    }

    public static Result wall() {
        int mpp = Play.application().configuration().getInt("messages.per.page");
        List<RMessage> messages = RMessage.getAll(0L, Long.valueOf(mpp));
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

    public static Result ajaxGetMessages(){
        DynamicForm requestData = Form.form().bindFromRequest();
        String tmpPage = requestData.get("page");
        int page = 0;

        if (tmpPage != null)
            page = Integer.valueOf(tmpPage);

        int mpp = Play.application().configuration().getInt("messages.per.page");
        Long offset = Long.valueOf(page * mpp);

        List<RMessage> messages = RMessage.getAll(offset, Long.valueOf(mpp));

        if (messages.isEmpty())
            return notFound();

        JSONArray list = new JSONArray();
        for(RMessage i: messages){
            System.out.println(i);
            JSONObject m = new JSONObject();
            m.put("id", i.getId());
            m.put("messages", i.getMessage());
            m.put("by", i.getBy());
            m.put("to", i.getTo());
            m.put("date", DateUtils.unixToStr(i.getDate()));

            list.add(m);
        }

        response().setContentType("text/javascript");
        return ok(list.toJSONString());
    }
  
}
