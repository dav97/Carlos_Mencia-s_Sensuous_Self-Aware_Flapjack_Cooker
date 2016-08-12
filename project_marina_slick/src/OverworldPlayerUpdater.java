import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

class PlayerUpdater
{
	private OverworldState overworldState;
	private OverworldModel overworldModel;
	private Input input;
	
	private boolean staleInputUp;
    private boolean staleInputUse;

	PlayerUpdater(OverworldState overworldState)
	{
		this.overworldState = overworldState;
		staleInputUp = false;
		staleInputUse = false;
	}

    void update(OverworldModel overworldModel, Input input) throws SlickException
    {
        this.overworldModel = overworldModel;
		this.input = input;
	
		float playerX = overworldModel.getPlayerX();
        float playerY = overworldModel.getPlayerY();
        float playerDX = overworldModel.getPlayerDX();
		float playerDY = overworldModel.getPlayerDY();

        float proposedPlayerDX = 0.0f;
        float proposedPlayerDY = 0.0f;
		float adjustedPlayerDX = 0.0f;
		float adjustedPlayerDY = 0.0f;

		boolean playerFloor = overworldModel.isPlayerCollisionDown();

        boolean playerInputLeft = input.isKeyDown(Input.KEY_A);
        boolean playerInputRight = input.isKeyDown(Input.KEY_D);
        boolean playerInputUp = input.isKeyDown(Input.KEY_W);
        boolean playerInputDown = input.isKeyDown(Input.KEY_S);
        boolean playerInputUse = input.isKeyDown(Input.KEY_E);
        
		checkOffWall(playerFloor);

		boolean playerOnWallLeft = overworldModel.isPlayerOnWallLeft();
        boolean playerOnWallRight = overworldModel.isPlayerOnWallRight();

		//if the player is not on solid ground, we need to calculate the effects of gravity
		if (!playerFloor)
		{	
			proposedPlayerDY = getProposedPlayerDYDueToGravity(playerDY,
															   playerOnWallLeft || playerOnWallRight);
        }
        
        //if the player is not trying to move left or right, fade her horizontal movement until stopped
		if (!(playerInputLeft || playerInputRight) &&
			(playerDX != 0.0f))
		{
            proposedPlayerDX = getProposedPlayerDXDueToFade(playerDX, playerFloor);
        }

        //player input
        //move left
        if (playerInputLeft && !playerInputRight)
        {
            if (playerDX > -(overworldModel.getMaxDXDueToInput()))
            {
                proposedPlayerDX = playerDX - (overworldModel.getDDXDueToInput());
            }
            else
            {
                proposedPlayerDX = playerDX;
            }
            if (overworldModel.isPlayerCollisionLeft() &&
				!overworldModel.isPlayerCollisionDown())
			{
                overworldModel.setPlayerOnWallLeft(true);
                //System.out.println("Player is on left wall");
            }
        }
        //move right
        if (playerInputRight && !playerInputLeft)
        {
            if (playerDX < overworldModel.getMaxDXDueToInput())
			{
                proposedPlayerDX = playerDX + overworldModel.getDDXDueToInput();
            }
            else
            {
                proposedPlayerDX = playerDX;
            }
            if (overworldModel.isPlayerCollisionRight() &&
				!overworldModel.isPlayerCollisionDown())
			{
                overworldModel.setPlayerOnWallRight(true);
                //System.out.println("Player is on right wall");
            }
        }
        //jump
        if (playerInputUp &&
			!staleInputUp)
		{
            if (playerFloor)
            { //if the player has solid ground beneath her
                staleInputUp = true;
                proposedPlayerDY = overworldModel.getInstantaneousJumpDY();
            }
            else if (playerOnWallLeft)
            {
                staleInputUp = true;
                overworldModel.setPlayerOnWallLeft(false);
                proposedPlayerDY = overworldModel.getInstantaneousWallJumpDY();
                proposedPlayerDX = overworldModel.getInstantaneousWallJumpRightDX();
            }
            else if (playerOnWallRight)
            {
                staleInputUp = true;
                overworldModel.setPlayerOnWallRight(false);
                proposedPlayerDY = overworldModel.getInstantaneousWallJumpDY();
                proposedPlayerDX = overworldModel.getInstantaneousWallJumpLeftDX();
            }
        }
        if (!playerInputUp)
        {
            staleInputUp = false;
        }
        //"move down"
        //will currently cause the player to let go of the wall and do nothing else
        if (playerInputDown)
        {
            overworldModel.setPlayerOnWallLeft(false);
            overworldModel.setPlayerOnWallRight(false);
        }
        //"use", currently disallowed while jumping or falling
        if (playerInputUse &&
			(playerDY == 0.0f) &&
			!staleInputUse)
		{
            staleInputUse = true;
            String mapHookCurrent = overworldModel.getMapHookCurrent();
            String mapHookPrevious = overworldModel.getMapHookSpawn();
            String hooks[] = overworldModel.getIntersectingTileHooks();
            //String feedback = "Intersecting tile hooks:";

            outerLoop:
            //TODO: naming loops to break multiple layers is discouraged, revisit
            for (String hook : hooks)
            {
                if (!hook.equals(""))
                {
                    for (int i_map_id = 0; i_map_id < OverworldGlobals.MAP_HOOK_LIST.length; ++i_map_id)
                    {
                        if (hook.equals(OverworldGlobals.MAP_HOOK_LIST[i_map_id]))
                        {
                            System.out.println("Valid map hook found: <" + hook + ">");
                            System.out.println("Transitioning to <" + hook + "> from <" + mapHookCurrent + ">");
                            overworldState.transitionMap(hook, mapHookCurrent);
                            return; //if we're transitioning, don't want to handle any other player action
                        }
                    }
                    //feedback += " " + hooks[i_hook];
                }
            }
            //System.out.println(feedback);
        }
        if (!playerInputUse)
        {
            staleInputUse = false;
        }
        //end player input

        //update player movement and location
        //if the proposed player dx is non-zero, check for collisions and use the collision distance for setting dx
        if (proposedPlayerDX != 0.0f)
        {
            adjustedDX = overworldModel.getHorizontalCollisionDistanceByDX(proposedPlayerDX);

            //if the proposed player movement was left or right but she ends up on a wall
            //(such as from faded movement or jump off of wall movement) set the playerOnWall
            //flag - allows player to jump back and forth between walls by just pressing the
            //jump key
            if (adjustedDX == 0.0f)
            {
                if (proposedPlayerDX < 0.0f)
                {
                    overworldModel.setPlayerOnWallLeft(true);
                }
                else
                {
                    overworldModel.setPlayerOnWallRight(true);
                }
            }
        }

        //if the proposed player dy is non-zero, check for collisions and use the collision distance for setting dy
        if (proposedPlayerDY != 0.0f)
        {
            adjustedDY = overworldModel.getVerticalCollisionDistanceByDY(proposedPlayerDY);
        }

        if (proposedPlayerDX != 0.0f &&
			proposedPlayerDY != 0.0f &&
			proposedPlayerDX == adjustedPlayerDX &&
			proposedPlayerDY == adjustedPlayerDY)
		{
            float[] dXdY = overworldModel.getDiagonalCollisionDistanceByDXAndDY(proposedPlayerDX, proposedPlayerDY);
            adjustedPlayerDX = dXdY[0];
            adjustedPlayerDY = dXdY[1];
        }
        
        proposedPlayerDX = adjustedPlayerDX;
        proposedPlayerDY = adjustedPlayerDY;

        overworldModel.setPlayerDX(proposedPlayerDX);
        overworldModel.setPlayerDY(proposedPlayerDY);

        overworldModel.setPlayerX(overworldModel.getPlayerX() + overworldModel.getPlayerDX());
        overworldModel.setPlayerY(overworldModel.getPlayerY() + overworldModel.getPlayerDY());
        //end update player movement and location
	}
	
	void checkOffWall(boolean floor)
	{
		//if there is not collision to the left of the player or the player is on solid ground,
        //unset the playerOnWallLeft flag
        if (!overworldModel.isPlayerCollisionLeft() ||
			floor)
        {
            overworldModel.setPlayerOnWallLeft(false);
        }
        //if there is not collision to the right of the player or the player is on solid ground,
        //unset the playerOnWallRight flag
        if (!overworldModel.isPlayerCollisionRight() ||
			floor)
        {
            overworldModel.setPlayerOnWallRight(false);
        }
	}

    float getProposedPlayerDYDueToGravity(float playerDY, boolean wall)
    {
        float proposedPlayerDY = 0.0f;
		float maxDYOnWall = 0.0f;
		float maxDYDueToGravity = 0.0f;
		
		//gravity
        //if the player is on a wall,
        if (wall)
        {
            maxDYOnWall = overworldModel.getMaxDYOnWall();
            //if the player dy is less than the max due to gravity while on a wall, add to it
            if (playerDY < maxDYOnWall)
            {
                proposedPlayerDY = playerDY + overworldModel.getDDYDueToGravity();
            }
            //else if the player dy is greater than the max due to gravity while on a wall, remove from it
            else if (playerDY > maxDYOnWall)
            {
                proposedPlayerDY = overworldModel.getPlayerDY() - overworldModel.getDDYDueToGravity();
            }
        }
        //else if the player is not on solid ground,
        else if (!overworldModel.isPlayerCollisionDown())
        {
			maxDYDueToGravity = overworldModel.getMaxDYDueToGravity();
            //if the player dy is less than the max dy due to gravity, add to it
            if (overworldModel.getPlayerDY() < maxDYDueToGravity)
            {
                proposedPlayerDY = playerDY + overworldModel.getDDYDueToGravity();
            }
            //else, just propose the last player dy
            else
            {
                proposedPlayerDY = playerDY;
            }
        }
        //end gravity
        
        return proposedPlayerDY;
	}

    float getProposedPlayerDXDueToFade(float playerDX, boolean floor)
    {
        float proposedPlayerDX = 0.0f;
	
		//horizontal player movement fade
        //if it's close enough to 0, just make it 0 - was experiencing a floating point error otherwise
        if ((playerDX < OverworldGlobals.STANDARD_DX_FADE_SANITY_BOUND) &&
			(playerDX > -(OverworldGlobals.STANDARD_DX_FADE_SANITY_BOUND)))
		{
            proposedPlayerDX = 0.0f;
        } else if (playerDX > 0.0f)
        {
            if (floor)
            {
                proposedPlayerDX = playerDX - overworldModel.getDDXDueToInput();
            }
            else
            {
                proposedPlayerDX = playerDX - (overworldModel.getDDXDueToInput() / 2.0f);
            }
        }
        else if (playerDX < 0.0f)
        {
            if (floor)
            {
                proposedPlayerDX = playerDX + overworldModel.getDDXDueToInput();
            }
            else
            {
                proposedPlayerDX = playerDX + (overworldModel.getDDXDueToInput() / 2.0f);
            }
        }
        //end horizontal player movement fade
        
        return proposedPlayerDX;
	}
}
