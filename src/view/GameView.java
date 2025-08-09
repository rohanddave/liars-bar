package view;

import model.game.GameState;
import view.ActionMenuView;
import view.StatusBarView;
import view.TableView;

public class GameView{
    private StatusBarView statusbar;
    private TableView tableview;
//    private PlayerHandView playerHand;
    private ActionMenuView actionMenu;
//    private ScreenManager ScreenManager;

    public void render(GameState GameState){}
    public void refresh(){}
    public void showMessage(String message){}
}