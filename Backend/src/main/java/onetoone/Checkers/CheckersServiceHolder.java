package onetoone.Checkers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CheckersServiceHolder {

    private static CheckersService checkersService;

    @Autowired
    public void setCheckersService(CheckersService checkersService) {
        CheckersServiceHolder.checkersService = checkersService;
    }

    public static CheckersService getCheckersService() {
        return checkersService;
    }
}