package com.tfc.liarsbar.model.commands;

import com.tfc.liarsbar.model.actions.ShowHandAction;

public class ShowHandCommand extends AbstractGameCommand {
  public ShowHandCommand(CommandRequest request) {
    super(request, new ShowHandAction());
  }

  @Override
  public String getDescription() {
    return "Show Hand";
  }
}
