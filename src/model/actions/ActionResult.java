package model.actions;

/**
 * Result of executing a game action
 */
public class ActionResult {
  private final boolean success;
  private final String message;
  private final Object data; // Optional additional data
  
  public ActionResult(boolean success, String message) {
    this(success, message, null);
  }
  
  public ActionResult(boolean success, String message, Object data) {
    this.success = success;
    this.message = message;
    this.data = data;
  }
  
  public boolean isSuccess() {
    return success;
  }
  
  public String getMessage() {
    return message;
  }
  
  public Object getData() {
    return data;
  }
  
  public static ActionResult success(String message) {
    return new ActionResult(true, message);
  }
  
  public static ActionResult success(String message, Object data) {
    return new ActionResult(true, message, data);
  }
  
  public static ActionResult failure(String message) {
    return new ActionResult(false, message);
  }
}