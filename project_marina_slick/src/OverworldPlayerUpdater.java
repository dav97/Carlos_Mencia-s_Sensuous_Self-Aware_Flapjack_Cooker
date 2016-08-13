import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

/**
 * PlayerUpdater is a class designed to handle processing
 * player input and environmental effects on the player
 * (i.e. gravity) and update the player's location and
 * movement in the model accordingly.
 *
 * @author scorple
 * @version 1.0
 * @since 2016.08.11
 */
class PlayerUpdater
{
	private OverworldState overworldState;
	private OverworldModel overworldModel;
	private Input input;
	
	private boolean staleInputUp;
    private boolean staleInputUse;

	/**
	 * Constructor. Initializes stale input flags to false
	 * and sets state callback reference.
     *
     * @param overworldState OverworldState: Callback reference,
	 * 		  needed for map transitions.
	 */
	PlayerUpdater(OverworldState overworldState)
	{
		this.overworldState = overworldState;
		staleInputUp = false;
		staleInputUse = false;
	}

	/**
	 * Given the current model and input, process effects on
	 * player location and update the model accordinly.
     *
     * @param overworldModel OverworldModel: The model pre-update,
	 * 		  needed for getting player location, previous movement
	 * 		  vector, and checking collisions.
	 * @param input Input: The player input object, needed for
	 * 		  checking input from the player.
	 */
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
            adjustedPlayerDX = overworldModel.getHorizontalCollisionDistanceByDX(proposedPlayerDX);
        }

        //if the proposed player dy is non-zero, check for collisions and use the collision distance for setting dy
        if (proposedPlayerDY != 0.0f)
        {
            adjustedPlayerDY = overworldModel.getVerticalCollisionDistanceByDY(proposedPlayerDY);
        }

        if (proposedPlayerDX != 0.0f &&
			proposedPlayerDY != 0.0f &&
			proposedPlayerDX == adjustedPlayerDX &&
			proposedPlayerDY == adjustedPlayerDY)
		{
            //System.out.println("proposedPlayerDX:<" + proposedPlayerDX + "> proposedPlayerDY:<" + proposedPlayerDY + ">");
            float[] dXdY = overworldModel.getDiagonalCollisionDistanceByDXAndDY(proposedPlayerDX, proposedPlayerDY);
            adjustedPlayerDX = dXdY[0];
            adjustedPlayerDY = dXdY[1];
        }

        //if the proposed player movement was left or right but she ends up on a wall
        //(such as from faded movement or jump off of wall movement) set the playerOnWall
        //flag - allows player to jump back and forth between walls by just pressing the
        //jump key
        if (adjustedPlayerDX == 0.0f)
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

        //            if (Math.abs(adjustedDX) > 0.1f) {
        //                System.out.println("adjustedDX:<" + adjustedDX + ">");
        //            }

        proposedPlayerDX = adjustedPlayerDX;
        proposedPlayerDY = adjustedPlayerDY;

        overworldModel.setPlayerDX(proposedPlayerDX);
        overworldModel.setPlayerDY(proposedPlayerDY);

        overworldModel.setPlayerX(overworldModel.getPlayerX() + overworldModel.getPlayerDX());
        overworldModel.setPlayerY(overworldModel.getPlayerY() + overworldModel.getPlayerDY());
        //end update player movement and location
	}
	
	/**
	 * Utility function for checking if the player is no longer
	 * on a wall and updating the model with that information.
     *
     * @param floor boolean: Expedites check, if floor is true
	 * 		  the player is definitely not on a wall.
	 */
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

	/**
	 * Utility function for getting the potential effects of
	 * gravity on the player.
     *
     * @param playerDY float: The previous player dY, needed for
	 * 		  checking against the maximum/minimum player dY and
	 * 		  calculating new dY.
	 * @param wall boolean: Wether or not the player is on a wall,
	 * 		  this will effect the ddY applied.
	 */
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

	/**
	 * Utility function for getting the potential horizontal
	 * movement fade - applied when the player is not providing
	 * a horizontal movement input, allows for a softer/gradual
	 * stop when the player releases input.
     *
     * @param playerDX float: The previous player dX, needed
	 * 		  for calculating new dX.
	 * @param floor boolean: Wether or not the player is on
	 * 		  solid ground, this will affect the ddX applied.
	 */
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
