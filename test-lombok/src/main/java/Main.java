import com.ashin.annotation.Getter;
import com.ashin.annotation.IKun;
import com.ashin.annotation.Setter;

@Getter
@Setter
public class Main {

    private String value = "ikun";

    @IKun
    public void say() {
    }

    public static void main(String[] args) {
        Main main = new Main();
        System.out.println(main.getValue());
        main.setValue("哇真的是你啊");
        System.out.println(main.getValue());
        main.say();
    }
}
