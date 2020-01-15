package dev.zmq.snakeshape;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.Random;

class SnakeView extends SurfaceView implements Runnable
{

    private Canvas m_Canvas;
    private SurfaceHolder m_Holder;
    private Paint m_Paint;
    private Rect m_Rect;
    private Context m_Context;

    private static Thread m_Thread=null;
    private static volatile Boolean m_Playing;

    public enum Direction{UP,RIGHT,DOWN,LEFT}
    private Direction m_Direction;

    //Mobile Screen Height & Width
    private int m_ScreenHeight;
    private int m_ScreenWidth;

    private int[]m_SnakeXs;
    private int[]m_SnakeYs;


    private int m_MouseX;
    private int m_MouseY;


    private int m_BlockSize;
    private int m_Score;

    private int m_SnakeLenght;

    private long m_NextFrameTime;
    private long FPS=10;

    private long MILLI_IN_A_SECONDS=1000;

    private final int NUM_BLOCKS_WIDE=40;
    private int m_numBlockHeight;

    public SnakeView(Context context, Point size)
    {
        super(context);
        m_ScreenWidth=size.x;
        m_ScreenHeight=size.y;

        m_BlockSize=m_ScreenWidth/NUM_BLOCKS_WIDE;
        m_numBlockHeight=m_ScreenHeight/m_BlockSize;

        m_Holder=getHolder();
        m_Paint=new Paint();

        m_SnakeXs=new int[200];
        m_SnakeYs=new int[200];

        startGame();

    }
    protected void startGame()
    {
        m_SnakeLenght=1;
        m_SnakeXs[0]=NUM_BLOCKS_WIDE/2;
        m_SnakeYs[0]=NUM_BLOCKS_WIDE/2;

        spaneMouse();

        m_Score=0;
        m_NextFrameTime=System.currentTimeMillis();
    }

    private void spaneMouse()
    {
        Random random=new Random();
        m_MouseX=random.nextInt(NUM_BLOCKS_WIDE-1)+1;
        m_MouseY=random.nextInt(NUM_BLOCKS_WIDE-1)+1;
    }

    @Override
    public void run()
    {
        while (m_Playing)
        {
            if (checkForUpdate())
            {
                updateGame();
                drawGame();
            }

        }

    }

    public static void pause()
    {
        m_Playing=false;
        try {

            {
                m_Thread.join();
            }

        } catch (InterruptedException e)
        {
            e.printStackTrace();
            //Error
        }
    }
    public static void resume()
    {
        m_Playing=true;
        {
            m_Thread=new Thread(); //this
            m_Thread.start();
        }
    }

    private boolean checkForUpdate()
    {

       if (m_NextFrameTime<=System.currentTimeMillis())
       {
           m_NextFrameTime=System.currentTimeMillis()+MILLI_IN_A_SECONDS/FPS;
           return true;
       }
       return false;
    }

    private void drawGame()
    {
        if (m_Holder.getSurface().isValid())
        {
            m_Canvas=m_Holder.lockCanvas();

            m_Canvas.drawColor(Color.argb(255,120,197,87));

            m_Paint=new Paint(Color.argb(255,255,255,255));

            m_Paint.setTextSize(30);

            m_Canvas.drawText("Score"+m_Score,10,20,m_Paint);

            for (int i=0;i<m_SnakeLenght;i++)
            {
               m_Canvas.drawRect(m_SnakeXs[i]*m_BlockSize,
                       (m_SnakeYs[i]*m_BlockSize),
                       (m_SnakeXs[i]*m_BlockSize)+m_BlockSize,
                       (m_SnakeYs[i]+m_BlockSize)+m_BlockSize,m_Paint);
            }
              m_Canvas.drawRect(m_MouseX * m_BlockSize,
                        (m_MouseY * m_BlockSize),
                        (m_MouseX * m_BlockSize) + m_BlockSize,
                        (m_MouseY + m_BlockSize) + m_BlockSize, m_Paint);


        }
        m_Holder.unlockCanvasAndPost(m_Canvas);

    }

    private void updateGame()
    {
        if(m_SnakeXs[0]==m_MouseX && m_SnakeYs[0]==m_MouseY)
        {
            eatMouse();
        }
        moveSnake();
        if (detectDeath())
        {
            startGame();
        }

    }

    private boolean detectDeath()
    {
       boolean dead=false;
       if(m_SnakeXs[0]==-1) dead=true;
       if (m_SnakeXs[0]>=NUM_BLOCKS_WIDE)dead=true;
        if(m_SnakeYs[0]==-1)dead=true;
        if (m_SnakeYs[0]>=NUM_BLOCKS_WIDE)dead=true;
        for(int i=m_SnakeLenght-1;i>0;i--)
        {
            if ((i>4)&& (m_SnakeXs[0]==m_SnakeXs[i]) && (m_SnakeYs[0]==m_SnakeYs[i]))
            {
                dead=true;
            }
        }
        return  true;
    }

    private void moveSnake()
    {
    for (int i=m_SnakeLenght;i>0;i++)
    {
        m_SnakeXs[i]=m_SnakeXs[i-1];
        m_SnakeYs[i]=m_SnakeYs[i-1];
    }
    switch (m_Direction)
    {
        case UP:
            m_SnakeYs[0]--;
            break;
        case RIGHT:
            m_SnakeXs[0]++;
            break;
        case DOWN:
            m_SnakeYs[0]++;
            break;
        case LEFT:
            m_SnakeXs[0]--;
            break;
    }
    }

    private void eatMouse()
    {
        m_SnakeLenght++;

        spaneMouse();

        m_Score=m_Score+1;
    }
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent)
    {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:

                if (motionEvent.getX() >= m_ScreenWidth / 2)
                    switch (m_Direction) {
                        case UP:
                            m_Direction = Direction.RIGHT;
                            break;
                        case RIGHT:
                            m_Direction = Direction.DOWN;
                            break;
                        case DOWN:
                            m_Direction = Direction.LEFT;
                            break;
                        case LEFT:
                            m_Direction = Direction.UP;
                            break;
                    }
                else
                    switch (m_Direction) {
                        case UP:
                            m_Direction = Direction.LEFT;
                            break;
                        case LEFT:
                            m_Direction = Direction.DOWN;
                            break;
                        case DOWN:
                            m_Direction = Direction.RIGHT;
                            break;
                        case RIGHT:
                            m_Direction = Direction.UP;
                            break;
                    }
        }

        return true;
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
