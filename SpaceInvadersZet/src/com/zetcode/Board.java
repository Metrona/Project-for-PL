package com.zetcode;

import com.zetcode.sprite.Alien;
import com.zetcode.sprite.Player;
import com.zetcode.sprite.Shot;
import com.zetcode.sprite.Barricade;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Board extends JPanel
{
    private Dimension d;
    private List<Alien> aliens;
    private List<Barricade> barricades;
    private Player player;
    private Shot shot;
    private int bombs;
    private int speed = 1;
    private int direction = -1;
    private int deaths = 0;

    private boolean inGame = true;
    private String explImg = "src/images/explosion.png";
    private String message = "Game Over";

    private Timer timer;

    public Board() 
    {
        initBoard();
        gameInit();
    }

    private void initBoard()
    {
        addKeyListener(new TAdapter());
        setFocusable(true);
        d = new Dimension(Commons.BOARD_WIDTH, Commons.BOARD_HEIGHT);
        setBackground(Color.black);

        timer = new Timer(Commons.DELAY, new GameCycle());
        timer.start();

        gameInit();
    }

    private void gameInit()
    {
        aliens = new ArrayList<>();
        for(int i = 0; i < 4; i++)
        {
            for(int j = 0; j < 6; j++)
            {
                var alien = new Alien(Commons.ALIEN_INIT_X + 18 * j, Commons.ALIEN_INIT_Y + 18 * i);
                aliens.add(alien);
            }
        }

        barricades = new ArrayList<>();
        for(int j = 0; j < 270; j++)
        {
            if(j < 54)
            {
                var barricade = new Barricade(Commons.BARRICADE_INIT_X + j, Commons.BARRICADE_INIT_Y);
                barricades.add(barricade);
            }
            else if(j >= 54 && j < 108)
            {
                var barricade = new Barricade(Commons.BARRICADE_INIT_X + 54 + j, Commons.BARRICADE_INIT_Y);
                barricades.add(barricade);
            }
            else if(j >= 108 && j < 162)
            {
                var barricade = new Barricade(Commons.BARRICADE_INIT_X + 108 + j, Commons.BARRICADE_INIT_Y);
                barricades.add(barricade);
            }
            else if(j >= 162 && j < 216)
            {
                var barricade = new Barricade(Commons.BARRICADE_INIT_X + 162 + j, Commons.BARRICADE_INIT_Y);
                barricades.add(barricade);
            }
            else if(j >= 216)
            {
                var barricade = new Barricade(Commons.BARRICADE_INIT_X + 216 + j, Commons.BARRICADE_INIT_Y);
                barricades.add(barricade);
            }
        }

        player = new Player();
        shot = new Shot();
    }

    private void drawAliens(Graphics g)
    {
        for (Alien alien : aliens)
        {
            if (alien.isVisible())
            {
                g.drawImage(alien.getImage(), alien.getX(), alien.getY(), this);
            }

            if (alien.isDying())
            {
                alien.die();
            }
        }
    }

    private void drawBarricade(Graphics g)
    {
        for (Barricade barricade : barricades)
        {
            if (barricade.isVisible())
            {
                g.drawImage(barricade.getImage(), barricade.getX(), barricade.getY(), this);
            }

            if (barricade.isDying())
            {
                barricade.die();
            }
        }
    }


    private void drawPlayer(Graphics g) 
    {
        if (player.isVisible())
        {
            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }

        if (player.isDying())
        {
            player.die();
            inGame = false;
        }
    }

    private void drawShot(Graphics g)
    {
        if (shot.isVisible())
        {
            g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
        }
    }

    private void drawBombing(Graphics g)
    {
        for (Alien a : aliens)
        {
            Alien.Bomb b = a.getBomb();

            if (!b.isDestroyed()) 
            {
                g.drawImage(b.getImage(), b.getX(), b.getY(), this);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) 
    {
        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);
        g.setColor(Color.green);

        if (inGame)
        {
            g.drawLine(0, Commons.GROUND,Commons.BOARD_WIDTH, Commons.GROUND);
            drawAliens(g);
            drawBarricade(g);
            drawPlayer(g);
            drawShot(g);
            drawBombing(g);
        } 
        else 
        {
            if (timer.isRunning()) 
            {
                timer.stop();
            }
            gameOver(g);
        }
        Toolkit.getDefaultToolkit().sync();
    }

    private void gameOver(Graphics g)
    {
        g.setColor(Color.black);
        g.fillRect(0, 0, Commons.BOARD_WIDTH, Commons.BOARD_HEIGHT);

        g.setColor(new Color(0, 32, 48));
        g.fillRect(50, Commons.BOARD_WIDTH / 2 - 30, Commons.BOARD_WIDTH - 100, 50);
        g.setColor(Color.white);
        g.drawRect(50, Commons.BOARD_WIDTH / 2 - 30, Commons.BOARD_WIDTH - 100, 50);

        var small = new Font("Helvetica", Font.BOLD, 14);
        var fontMetrics = this.getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(message, (Commons.BOARD_WIDTH - fontMetrics.stringWidth(message)) / 2,Commons.BOARD_WIDTH / 2);
    }

    private void update()
    {
        if(deaths == Commons.NUMBER_OF_ALIENS_TO_DESTROY)
        {
            inGame = false;
            timer.stop();
            message = "Game won!";
        }

        player.act();

        if (shot.isVisible())
        {
            int shotX = shot.getX();
            int shotY = shot.getY();

            for (Alien alien : aliens)
            {
                int alienX = alien.getX();
                int alienY = alien.getY();

                if (alien.isVisible() && shot.isVisible())
                {
                    if (shotX >= (alienX) && shotX <= (alienX + Commons.ALIEN_WIDTH) && shotY >= (alienY) && shotY <= (alienY + Commons.ALIEN_HEIGHT))
                    {
                        var ii = new ImageIcon(explImg);
                        alien.setImage(ii.getImage());
                        alien.setDying(true);
                        deaths++;
                        shot.die();
                        if(deaths >= 12 && speed < 2)
                        {
                            speed = speed + 1;
                            if(direction == -1)
                            {
                                direction = direction - 1;
                            }
                            else if (direction == 1)
                            {
                                direction = direction + 1;
                            }
                        }
                        else if(deaths >= 18 && speed < 3)
                        {
                            speed = speed + 1;
                            if(direction == -2)
                            {
                                direction = direction - 1;
                            }
                            else if (direction == 2)
                            {
                                direction = direction + 1;
                            }
                        }
                    }
                }
            }

            int y = shot.getY();
            y -= 4;

            if (y < 0) 
            {
                shot.die();
            } 
            else 
            {
                shot.setY(y);
            }
        }

        for (Alien alien : aliens)
        {
            int x = alien.getX();
            if (x >= Commons.BOARD_WIDTH - Commons.BORDER_RIGHT && direction != -1)
            {
                direction = -speed;
                Iterator<Alien> i1 = aliens.iterator();
                while (i1.hasNext())
                {
                    Alien a2 = i1.next();
                    a2.setY(a2.getY() + Commons.GO_DOWN);
                }
            }

            if (x <= Commons.BORDER_LEFT && direction != 1) {
                direction = speed;
                Iterator<Alien> i2 = aliens.iterator();
                while (i2.hasNext())
                {
                    Alien a = i2.next();
                    a.setY(a.getY() + Commons.GO_DOWN);
                }
            }
        }

        Iterator<Alien> it = aliens.iterator();

        while (it.hasNext())
        {
            Alien alien = it.next();
            if (alien.isVisible())
            {
                int y = alien.getY();
                if (y > Commons.GROUND - Commons.ALIEN_HEIGHT)
                {
                    inGame = false;
                    message = "Invasion!";
                }
                alien.act(direction);
            }
        }

        var generator = new Random();

        for (Alien alien : aliens)
        {
            int shot = generator.nextInt(15);
            Alien.Bomb bomb = alien.getBomb();
            if(bombs < 6 || deaths >= 12)
            {
                if (shot == Commons.CHANCE && alien.isVisible() && bomb.isDestroyed())
                {
                    bombs = bombs + 1;
                    bomb.setDestroyed(false);
                    bomb.setX(alien.getX());
                    bomb.setY(alien.getY());
                }
            }

            int bombX = bomb.getX();
            int bombY = bomb.getY();
            int playerX = player.getX();
            int playerY = player.getY();

            if (player.isVisible() && !bomb.isDestroyed())
            {
                if (bombX >= (playerX) && bombX <= (playerX + Commons.PLAYER_WIDTH) && bombY >= (playerY) && bombY <= (playerY + Commons.PLAYER_HEIGHT))
                {
                    var ii = new ImageIcon(explImg);
                    player.setImage(ii.getImage());
                    player.setDying(true);
                    bomb.setDestroyed(true);
                    bombs = bombs - 1;
                }
            }

            for (Barricade barricade : barricades)
            {
                int barricadeX = barricade.getX();
                int barricadeY = barricade.getY();

                if (barricade.isVisible() && !bomb.isDestroyed())
                {
                    if (bombX >= (barricadeX) && bombX <= (barricadeX + Commons.BARRICADE_WIDTH) && bombY >= (barricadeY) && bombY <= (barricadeY + Commons.BARRICADE_HEIGHT))
                    {
                        var ii = new ImageIcon(explImg);
                        barricade.setImage(ii.getImage());
                        barricade.setDying(true);
                        bomb.setDestroyed(true);
                        bombs = bombs - 1;
                    }
                }
            }


            if (!bomb.isDestroyed())
            {
                bomb.setY(bomb.getY() + 1);
                if (bomb.getY() >= Commons.GROUND - Commons.BOMB_HEIGHT)
                {
                    bomb.setDestroyed(true);
                    bombs = bombs - 1;
                }
            }
        }
    }

    private void doGameCycle()
    {
        update();
        repaint();
    }

    private class GameCycle implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            doGameCycle();
        }
    }

    private class TAdapter extends KeyAdapter
    {
        @Override
        public void keyReleased(KeyEvent e)
        {
            player.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e)
        {
            player.keyPressed(e);
            int x = player.getX();
            int y = player.getY();
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_SPACE)
            {
                if (inGame)
                {
                    if (!shot.isVisible())
                    {
                        shot = new Shot(x, y);
                    }
                }
            }
        }
    }
}
