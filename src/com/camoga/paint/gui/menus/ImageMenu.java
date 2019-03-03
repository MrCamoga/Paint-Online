package com.camoga.paint.gui.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class ImageMenu extends JMenu implements ActionListener {
	public ImageMenu(String text) {
		super(text);

		JMenuItem canvasSize = new JMenuItem("Canvas Size");
		JMenuItem scale = new JMenuItem("Scale Image");
		
		JMenu grid = new JMenu("Grid");
			JMenuItem activate = new JMenuItem("Activate/Deactivate");
			JMenuItem settings = new JMenuItem("Settings");
			
			activate.addActionListener(this);
			settings.addActionListener(this);
			grid.add(activate);
			grid.add(settings);		

		canvasSize.addActionListener(this);
		scale.addActionListener(this);
		grid.addActionListener(this);
		add(canvasSize);
		add(scale);
		add(grid);
	}

	public void actionPerformed(ActionEvent e) {
		//TODO
		// Byte code:
		// 0: aload_1
		// 1: invokevirtual 50 java/awt/event/ActionEvent:getActionCommand
		// ()Ljava/lang/String;
		// 4: dup
		// 5: astore_2
		// 6: invokevirtual 56 java/lang/String:hashCode ()I
		// 9: lookupswitch default:+280->289, -1058673904:+51->60, -652091707:+63->72,
		// -263909943:+75->84, 2228070:+87->96, 1499275331:+99->108
		// 60: aload_2
		// 61: ldc 21
		// 63: invokevirtual 62 java/lang/String:equals (Ljava/lang/Object;)Z
		// 66: ifne +223 -> 289
		// 69: goto +220 -> 289
		// 72: aload_2
		// 73: ldc 17
		// 75: invokevirtual 62 java/lang/String:equals (Ljava/lang/Object;)Z
		// 78: ifne +211 -> 289
		// 81: goto +208 -> 289
		// 84: aload_2
		// 85: ldc 14
		// 87: invokevirtual 62 java/lang/String:equals (Ljava/lang/Object;)Z
		// 90: ifne +30 -> 120
		// 93: goto +196 -> 289
		// 96: aload_2
		// 97: ldc 19
		// 99: invokevirtual 62 java/lang/String:equals (Ljava/lang/Object;)Z
		// 102: ifne +187 -> 289
		// 105: goto +184 -> 289
		// 108: aload_2
		// 109: ldc 23
		// 111: invokevirtual 62 java/lang/String:equals (Ljava/lang/Object;)Z
		// 114: ifne +175 -> 289
		// 117: goto +172 -> 289
		// 120: new 66 javax/swing/JPanel
		// 123: dup
		// 124: new 68 java/awt/GridLayout
		// 127: dup
		// 128: iconst_2
		// 129: iconst_2
		// 130: invokespecial 70 java/awt/GridLayout:<init> (II)V
		// 133: invokespecial 73 javax/swing/JPanel:<init> (Ljava/awt/LayoutManager;)V
		// 136: astore_3
		// 137: new 76 javax/swing/JSpinner
		// 140: dup
		// 141: new 78 javax/swing/SpinnerNumberModel
		// 144: dup
		// 145: bipush 64
		// 147: iconst_1
		// 148: sipush 256
		// 151: iconst_1
		// 152: invokespecial 80 javax/swing/SpinnerNumberModel:<init> (IIII)V
		// 155: invokespecial 83 javax/swing/JSpinner:<init>
		// (Ljavax/swing/SpinnerModel;)V
		// 158: astore 4
		// 160: new 76 javax/swing/JSpinner
		// 163: dup
		// 164: new 78 javax/swing/SpinnerNumberModel
		// 167: dup
		// 168: bipush 64
		// 170: iconst_1
		// 171: sipush 256
		// 174: iconst_1
		// 175: invokespecial 80 javax/swing/SpinnerNumberModel:<init> (IIII)V
		// 178: invokespecial 83 javax/swing/JSpinner:<init>
		// (Ljavax/swing/SpinnerModel;)V
		// 181: astore 5
		// 183: new 76 javax/swing/JSpinner
		// 186: dup
		// 187: new 78 javax/swing/SpinnerNumberModel
		// 190: dup
		// 191: iconst_0
		// 192: iconst_0
		// 193: aload 4
		// 195: invokevirtual 86 javax/swing/JSpinner:getValue ()Ljava/lang/Object;
		// 198: checkcast 90 java/lang/Integer
		// 201: invokevirtual 92 java/lang/Integer:intValue ()I
		// 204: bipush 64
		// 206: isub
		// 207: iconst_1
		// 208: invokespecial 80 javax/swing/SpinnerNumberModel:<init> (IIII)V
		// 211: invokespecial 83 javax/swing/JSpinner:<init>
		// (Ljavax/swing/SpinnerModel;)V
		// 214: astore 6
		// 216: new 76 javax/swing/JSpinner
		// 219: dup
		// 220: new 78 javax/swing/SpinnerNumberModel
		// 223: dup
		// 224: iconst_0
		// 225: iconst_0
		// 226: aload 5
		// 228: invokevirtual 86 javax/swing/JSpinner:getValue ()Ljava/lang/Object;
		// 231: checkcast 90 java/lang/Integer
		// 234: invokevirtual 92 java/lang/Integer:intValue ()I
		// 237: bipush 64
		// 239: isub
		// 240: iconst_1
		// 241: invokespecial 80 javax/swing/SpinnerNumberModel:<init> (IIII)V
		// 244: invokespecial 83 javax/swing/JSpinner:<init>
		// (Ljavax/swing/SpinnerModel;)V
		// 247: astore 7
		// 249: aload_3
		// 250: aload 4
		// 252: invokevirtual 95 javax/swing/JPanel:add
		// (Ljava/awt/Component;)Ljava/awt/Component;
		// 255: pop
		// 256: aload_3
		// 257: aload 6
		// 259: invokevirtual 95 javax/swing/JPanel:add
		// (Ljava/awt/Component;)Ljava/awt/Component;
		// 262: pop
		// 263: aload_3
		// 264: aload 5
		// 266: invokevirtual 95 javax/swing/JPanel:add
		// (Ljava/awt/Component;)Ljava/awt/Component;
		// 269: pop
		// 270: aload_3
		// 271: aload 7
		// 273: invokevirtual 95 javax/swing/JPanel:add
		// (Ljava/awt/Component;)Ljava/awt/Component;
		// 276: pop
		// 277: getstatic 98 com/camoga/paint/gui/Window:window
		// Lcom/camoga/paint/gui/Window;
		// 280: aload_3
		// 281: ldc 104
		// 283: iconst_2
		// 284: invokestatic 106 javax/swing/JOptionPane:showConfirmDialog
		// (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;I)I
		// 287: istore 8
		// 289: return
		// Line number table:
		// Java source line #42 -> byte code offset #0
		// Java source line #44 -> byte code offset #120
		// Java source line #45 -> byte code offset #137
		// Java source line #46 -> byte code offset #160
		// Java source line #47 -> byte code offset #183
		// Java source line #48 -> byte code offset #216
		// Java source line #49 -> byte code offset #249
		// Java source line #50 -> byte code offset #256
		// Java source line #51 -> byte code offset #263
		// Java source line #52 -> byte code offset #270
		// Java source line #53 -> byte code offset #277
		// Java source line #66 -> byte code offset #289
		// Local variable table:
		// start length slot name signature
		// 0 290 0 this ImageMenu
		// 0 290 1 e java.awt.event.ActionEvent
		// 5 104 2 str String
		// 136 145 3 panel javax.swing.JPanel
		// 158 93 4 width javax.swing.JSpinner
		// 181 84 5 height javax.swing.JSpinner
		// 214 44 6 x javax.swing.JSpinner
		// 247 25 7 y javax.swing.JSpinner
		// 287 1 8 i int
	}
}