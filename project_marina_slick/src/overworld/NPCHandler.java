package overworld;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.ResourceLoader;
import overworld.model.Model;
import overworld.view.View;

import java.io.IOException;
import java.io.InputStream;

import static org.newdawn.slick.Image.FILTER_NEAREST;
import static overworld.Globals.*;

/**
 * Created by Scorple on 9/3/2016.
 */
public class NPCHandler
{
    private String ref;

    private Presenter presenter;
    private Model     model;
    private View      view;

    //private overworld.model.NPC modelNPC;
    //private overworld.view.NPC  viewNPC;

    NPCHandler(Presenter presenter, Model model, View view, String ref, long x, long y) throws SlickException
    {
        this.ref = ref;

        this.presenter = presenter;
        this.model = model;
        this.view = view;

        setupModel(ref, x, y);
        setupView(ref);
    }

    private void setupModel(String ref, long x, long y)
    {
        SAXBuilder saxBuilder = new SAXBuilder();
        InputStream inputStream =
            ResourceLoader.getResourceAsStream(NPC_RESOURCE_PATH + ref + NPC_PROPERTIES_FILE);
        Document            document = null;
        overworld.model.NPC npc      = new overworld.model.NPC(x, y);

        try
        {
            document = saxBuilder.build(inputStream);
        }
        catch (JDOMException | IOException e)
        {
            e.printStackTrace();
        }

        Element npcElement = document.getRootElement();

        Element generalElement = npcElement.getChild("general");

        npc.setWidth(Long.parseLong(generalElement.getChildText("width")));
        npc.setHeight(Long.parseLong(generalElement.getChildText("height")));

        model.addEntity(ref, npc);

        //modelNPC = npc;
    }

    private void setupView(String ref) throws SlickException
    {
        InputStream        inputStream;
        overworld.view.NPC npc = new overworld.view.NPC();

        Image[] npcFramesFaceLeft = new Image[NPC_GRAPHIC_FRAME_COUNT_FACE];
        for (int i = 0; i < NPC_GRAPHIC_FRAME_COUNT_FACE; ++i)
        {
            inputStream = ResourceLoader.getResourceAsStream(
                NPC_RESOURCE_PATH + ref + GRAPHIC_DIRECTORY_STANDARD + NPC_GRAPHIC_PREFIX_LEFT + (i + 1) +
                GRAPHICS_EXTENSION);
            npcFramesFaceLeft[i] = new Image(inputStream,
                                             NPC_RESOURCE_PATH + ref + GRAPHIC_DIRECTORY_STANDARD +
                                             NPC_GRAPHIC_PREFIX_LEFT + (i + 1) + GRAPHICS_EXTENSION,
                                             false,
                                             FILTER_NEAREST);
        }
        //there is only one facing frame, do not auto-update the animation
        Animation npcAnimationFaceLeft = new Animation(npcFramesFaceLeft, NPC_GRAPHIC_FRAME_DURACTION_FACE, false);

        Image[] npcFramesFaceRight = new Image[NPC_GRAPHIC_FRAME_COUNT_FACE];
        for (int i = 0; i < NPC_GRAPHIC_FRAME_COUNT_FACE; ++i)
        {
            inputStream = ResourceLoader.getResourceAsStream(
                NPC_RESOURCE_PATH + ref + GRAPHIC_DIRECTORY_STANDARD + NPC_GRAPHIC_PREFIX_LEFT + (i + 1) +
                GRAPHICS_EXTENSION);
            //load right frames as flipped left frames
            npcFramesFaceRight[i] = new Image(inputStream,
                                              NPC_RESOURCE_PATH + ref + GRAPHIC_DIRECTORY_STANDARD +
                                              NPC_GRAPHIC_PREFIX_LEFT + (i + 1) + GRAPHICS_EXTENSION,
                                              true,
                                              FILTER_NEAREST);
        }
        //there is only one facing frame, do not auto-update the animation
        Animation npcAnimationFaceRight = new Animation(npcFramesFaceRight, NPC_GRAPHIC_FRAME_DURACTION_FACE, false);

        Image[] npcFramesWalkLeft = new Image[NPC_GRAPHIC_FRAME_COUNT_WALK];
        for (int i = 0; i < NPC_GRAPHIC_FRAME_COUNT_WALK; ++i)
        {
            inputStream = ResourceLoader.getResourceAsStream(
                NPC_RESOURCE_PATH + ref + GRAPHIC_DIRECTORY_STANDARD + NPC_GRAPHIC_PREFIX_LEFT + (i + 1) +
                GRAPHICS_EXTENSION);
            npcFramesWalkLeft[i] = new Image(inputStream, NPC_RESOURCE_PATH + ref
                                                          + GRAPHIC_DIRECTORY_STANDARD + NPC_GRAPHIC_PREFIX_LEFT +
                                                          (i + 1) + GRAPHICS_EXTENSION, false, FILTER_NEAREST);
        }
        Animation npcAnimationWalkLeft = new Animation(npcFramesWalkLeft, NPC_GRAPHIC_FRAME_DURATION_WALK, true);

        Image[] npcFramesWalkRight = new Image[NPC_GRAPHIC_FRAME_COUNT_WALK];
        for (int i = 0; i < NPC_GRAPHIC_FRAME_COUNT_WALK; ++i)
        {
            inputStream = ResourceLoader.getResourceAsStream(
                NPC_RESOURCE_PATH + ref + GRAPHIC_DIRECTORY_STANDARD + NPC_GRAPHIC_PREFIX_LEFT + (i + 1) +
                GRAPHICS_EXTENSION);
            //load right frames as flipped left frames
            npcFramesWalkRight[i] = new Image(inputStream, NPC_RESOURCE_PATH + ref
                                                           + GRAPHIC_DIRECTORY_STANDARD + NPC_GRAPHIC_PREFIX_LEFT +
                                                           (i + 1) + GRAPHICS_EXTENSION, true, FILTER_NEAREST);
        }
        Animation npcAnimationWalkRight = new Animation(npcFramesWalkRight, NPC_GRAPHIC_FRAME_DURATION_WALK, true);

        npc.setDefaultGraphic(npcAnimationFaceLeft);
        npc.setAnimationFaceLeft(npcAnimationFaceLeft);
        npc.setAnimationFaceRight(npcAnimationFaceRight);
        npc.setAnimationWalkLeft(npcAnimationWalkLeft);
        npc.setAnimationWalkRight(npcAnimationWalkRight);

        view.addEntity(ref, npc);

        //viewNPC = npc;
    }

    public void updateView(float mapRenderX, float mapRenderY)
    {
        overworld.model.NPC modelNPC = (overworld.model.NPC) model.getEntityByRef(ref);
        overworld.view.NPC  viewNPC  = (overworld.view.NPC) view.getEntityByRef(ref);

        viewNPC.setX((float) (modelNPC.getX() / GRAPHIC_TO_LOGIC_CONVERSION) + mapRenderX);
        viewNPC.setY((float) (modelNPC.getY() / GRAPHIC_TO_LOGIC_CONVERSION) + mapRenderY);

        //        System.out.println("mapRenderX:<" + mapRenderX + ">");
        //        System.out.println("mapRenderY:<" + mapRenderY + ">");
        //        System.out.println("modelNPC.getX():<" + modelNPC.getX() + ">");
        //        System.out.println("modelNPC.getY():<" + modelNPC.getY() + ">");
        //        System.out.println("((float)(modelNPC.getX() / GRAPHIC_TO_LOGIC_CONVERSION) - mapRenderX):<" + ((float)(modelNPC.getX() / GRAPHIC_TO_LOGIC_CONVERSION) - mapRenderX) + ">");
        //        System.out.println("((float)(modelNPC.getY() / GRAPHIC_TO_LOGIC_CONVERSION) - mapRenderY):<" + ((float)(modelNPC.getY() / GRAPHIC_TO_LOGIC_CONVERSION) - mapRenderY) + ">");
    }
}
