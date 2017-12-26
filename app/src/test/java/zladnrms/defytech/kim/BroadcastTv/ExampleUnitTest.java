package zladnrms.defytech.kim.BroadcastTv;

import org.junit.Test;

import rx.Observable;
import rx.Subscriber;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void from_테스트() {
        System.out.println("Create Observable");

        //Observable<String> observable = Observable.just("개미");
        Observable<String> observable = Observable.from(new String[] { "개미", "매", "마루" });

        System.out.println("Do Subscribe");

        observable.subscribe(new Subscriber<String>() {
            @Override public void onNext(String text) {
                System.out.println("onNext : " + text);
            }

            @Override public void onCompleted() {
                System.out.println("onCompleted");
            }

            @Override public void onError(Throwable e) {
                System.out.println("onError : " + e.getMessage());
            }
        });
    }
}