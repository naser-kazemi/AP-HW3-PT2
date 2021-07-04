import View.ChatMenu;
import View.Menu;
import View.UserConfigMenu;

import java.util.Scanner;

public class ProgramRunner {

    public ProgramRunner() {

    }

    public void run() {

        UserConfigMenu userConfigMenu = new UserConfigMenu();
        ChatMenu chatMenu = new ChatMenu();

        Scanner input = new Scanner(System.in);

        while (true) {
            if (Menu.getCurrentMenu() instanceof UserConfigMenu)
                userConfigMenu.run(input);
            else if (Menu.getCurrentMenu() instanceof ChatMenu)
                chatMenu.run(input);
        }
    }


}
