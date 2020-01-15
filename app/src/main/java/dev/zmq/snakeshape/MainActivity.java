package dev.zmq.snakeshape;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;

import java.util.EventListener;

public class MainActivity extends Activity
{
   SnakeView snakeView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Display display=getWindowManager().getDefaultDisplay();
        Point size=new Point();
        display.getSize(size);

        snakeView=new SnakeView(this,size);
        setContentView(snakeView);
        //snakeView.startGame();

    }
    @Override
    public void onResume()
    {
        super.onResume();
        SnakeView.resume();
    }
    @Override
    public void onPause()
    {
        super.onPause();
        SnakeView.pause();
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent)
    {
        if (keyEvent.getAction()==KeyEvent.ACTION_DOWN)
        {
            switch(keyEvent.getKeyCode())
            {
                case KeyEvent.KEYCODE_BACK:
                    return true;
            }
        }
        return super.dispatchKeyEvent(keyEvent);
    }

}

