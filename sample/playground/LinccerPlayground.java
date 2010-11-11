package playground;

import org.json.JSONObject;

import com.hoccer.api.BadModeException;
import com.hoccer.api.ClientActionException;
import com.hoccer.api.ClientCreationException;
import com.hoccer.api.ClientDescription;
import com.hoccer.api.CollidingActionsException;
import com.hoccer.api.Linccer;
import com.hoccer.api.UpdateException;

public class LinccerPlayground {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        
        try {
            final Linccer linccer = new Linccer(new ClientDescription("PlaygroundLinccer"));
            new Thread(new EnvironmentUpdater(linccer)).start();
            
            while (true) {
                try {            
                    System.out.println("starting waiting");
                    JSONObject received = linccer.receive("1:n", "waiting=true");
                    System.out.println(received);
                } catch (BadModeException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                
                } catch (ClientActionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                
                } catch (CollidingActionsException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }  
        } catch (ClientCreationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
    
    
    public static class EnvironmentUpdater implements Runnable {
        Linccer mLinccer;
        public EnvironmentUpdater(Linccer linccer) {
            mLinccer = linccer;
        }
        public void run() {
            try {
                while (true) {
                    mLinccer.onGpsChanged(52.5167780325, 13.409039925, 1000);
                    Thread.sleep(30 * 1000);
                }

            } catch (UpdateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }            
   }
    

}
