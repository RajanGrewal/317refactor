package com.jagex.runescape;

public final class Animation {

	public static Animation forFrameId(int frameId) {
		if (animations == null)
			return null;
		else
			return animations[frameId];
	}

	public static void init(int size) {
		animations = new Animation[size + 1];
		opaque = new boolean[size + 1];
		for (int i = 0; i < size + 1; i++)
			opaque[i] = true;

	}

	public static boolean isNullFrame(int frameId) {
		return frameId == -1;
	}

	public static void method529(byte data[]) {
		Buffer buffer = new Buffer(data);
		buffer.position = data.length - 8;

		int attributesOffset = buffer.getUnsignedLEShort();
		int transformationOffset = buffer.getUnsignedLEShort();
		int durationOffset = buffer.getUnsignedLEShort();
		int baseOffset = buffer.getUnsignedLEShort();

		int offset = 0;
		Buffer headerBuffer = new Buffer(data);
		headerBuffer.position = offset;

		offset += attributesOffset + 2;
		Buffer attributeBuffer = new Buffer(data);
		attributeBuffer.position = offset;

		offset += transformationOffset;
		Buffer transformationBuffer = new Buffer(data);
		transformationBuffer.position = offset;

		offset += durationOffset;
		Buffer durationBuffer = new Buffer(data);
		durationBuffer.position = offset;

		offset += baseOffset;
		Buffer baseBuffer = new Buffer(data);
		baseBuffer.position = offset;

		Skins base = new Skins(baseBuffer);
		int count = headerBuffer.getUnsignedLEShort();

		int transformationIndices[] = new int[500];
		int transformX[] = new int[500];
		int transformY[] = new int[500];
		int transformZ[] = new int[500];

		for (int i = 0; i < count; i++) {
			int id = headerBuffer.getUnsignedLEShort();

			Animation anim = animations[id] = new Animation();
			anim.displayLength = durationBuffer.getUnsignedByte();
			anim.animationSkins = base;

			int transformationCount = headerBuffer.getUnsignedByte();
			int highestIndex = -1;
			int transformation = 0;

			for (int index = 0; index < transformationCount; index++) {
				int attribute = attributeBuffer.getUnsignedByte();

				if (attribute > 0) {
					if (base.opcodes[index] != 0) {
						for (int next = index - 1; next > highestIndex; next--) {
							if (base.opcodes[next] != 0)
								continue;
							transformationIndices[transformation] = next;
							transformX[transformation] = 0;
							transformY[transformation] = 0;
							transformZ[transformation] = 0;
							transformation++;
							break;
						}

					}
					transformationIndices[transformation] = index;

					int standard = base.opcodes[index] == 3 ? 128 : 0;

					if ((attribute & 1) != 0)
						transformX[transformation] = transformationBuffer.getSmartA();
					else
						transformX[transformation] = standard;

					if ((attribute & 2) != 0)
						transformY[transformation] = transformationBuffer.getSmartA();
					else
						transformY[transformation] = standard;

					if ((attribute & 4) != 0)
						transformZ[transformation] = transformationBuffer.getSmartA();
					else
						transformZ[transformation] = standard;

					highestIndex = index;

					transformation++;

					if (base.opcodes[index] == 5)
						opaque[id] = false;
				}
			}

			anim.frameCount = transformation;
			anim.opcodeTable = new int[transformation];
			anim.transformationX = new int[transformation];
			anim.transformationY = new int[transformation];
			anim.transformationZ = new int[transformation];

			for (int t = 0; t < transformation; t++) {
				anim.opcodeTable[t] = transformationIndices[t];
				anim.transformationX[t] = transformX[t];
				anim.transformationY[t] = transformY[t];
				anim.transformationZ[t] = transformZ[t];
			}
		}
	}

	public static void nullLoader() {
		animations = null;
	}

	private static Animation[] animations;
	public int displayLength;
	public Skins animationSkins;
	public int frameCount;
	public int opcodeTable[];
	public int transformationX[];
	public int transformationY[];
	public int transformationZ[];
	private static boolean[] opaque;
}