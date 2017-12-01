package framework.animation;

import java.util.ArrayList;

import framework.model3D.Position3D;
import framework.model3D.Quaternion3D;

/**
 * 階層化されたオブジェクトに対するアニメーション情報を保持する（部品単位で位置、向き、テクスチャをアニメーション可能）
 * @author 新田直也
 *
 */
public class Animation3D {
	public long time = 0;
	
	private ArrayList<PartAnimation> partList = new ArrayList<PartAnimation>();
	private long maxKey = getMaxKey();

	public Animation3D() {
		time = 0;
	}

	public Animation3D(Animation3D a) {
		time = 0;
		partList = a.partList;
		maxKey = a.maxKey;
	}

	public boolean equals(Animation3D a) {
		return (partList == a.partList && maxKey == a.maxKey);
	}

	public void reset() {
		time = 0;
	}

	public boolean progress(long interval) {
		if (maxKey == 0)
			return true; // 空のアニメーションの場合動かさない
		time += interval;
		// System.out.println(time + "/" + maxKey);
		if (time > maxKey) {
			time = time % maxKey; // timeが最後の要素のkeyの値（アニメーションの最後のkey)を超えている場合、tを最後のkeyの値（maxKey)で割った余りとして設定する
			return false;
		} else
			return true;
	}

	public Pose3D getPose() {
		if (maxKey == 0 || partList.size() == 0)
			return new DefaultPose3D();

		KeyFrame[] aroundKey = new KeyFrame[2];
		Quaternion3D q;
		Position3D p;
		Quaternion3D tq;
		Position3D tp;
		Pose3D pose = new Pose3D();
		for (int i = 0; i < partList.size(); i++) {
			aroundKey = partList.get(i).getKey(time);

			// コピーコンストラクタの作成
			q = null;
			p = null;
			tq = null;
			tp = null;
			if (aroundKey[0].getPosition() != null) {
				p = new Position3D(aroundKey[0].getPosition()); // getPosition()が参照を返すのでコピーしてから変更
			}
			if (aroundKey[0].getQuaternion() != null) {
				q = new Quaternion3D(aroundKey[0].getQuaternion()); // getQuaternion()がアドレス（参照）を返すので、コピー（＝ｑ）を作成してそちらを変更している。
			}
			if (aroundKey[0].getTexturePosition() != null) {
				tp = new Position3D(aroundKey[0].getTexturePosition()); // getPosition()が参照を返すのでコピーしてから変更
			}
			if (aroundKey[0].getTextureQuaternion() != null) {
				tq = new Quaternion3D(aroundKey[0].getTextureQuaternion()); // getQuaternion()がアドレス（参照）を返すので、コピー（＝ｑ）を作成してそちらを変更している。
			}

			// timeがkeyそのものだった場合（補間の計算が不要な場合）
			if (aroundKey[1] != null) {
				// t1 はtの前のkey（aroundKey[0]）のスカラー倍
				double t1 = aroundKey[1].key - time;
				// t2 はtの後のkey（aroundKey[1]）のスカラー倍
				double t2 = time - aroundKey[0].key;
				double t3 = aroundKey[1].key - aroundKey[0].key;
				double timealpha = t2 / t3;

				// timeに対するQuaternionとPositionの計算
				if (p != null) {
					Position3D p2 = new Position3D(aroundKey[1].getPosition());
					p.mul(t1 / t3).add(p2.mul(t2 / t3));
				}
				if (q != null) {
					Quaternion3D q2 = new Quaternion3D(aroundKey[1].getQuaternion());
					q.getInterpolate(q2, timealpha);
				}
				if (tp != null) {
					Position3D tp2 = new Position3D(aroundKey[1].getTexturePosition());
					tp.mul(t1 / t3).add(tp2.mul(t2 / t3));
				}
				if (tq != null) {
					Quaternion3D tq2 = new Quaternion3D(aroundKey[1].getTextureQuaternion());
					tq.getInterpolate(tq2, timealpha);
				}
			}
			pose.addPose(partList.get(i).getName(), p, q, aroundKey[0].getTexture(), tp, tq, partList.get(i).getTextureUnit());
		}

		return pose;
	}

	public Animation3D merge(Animation3D a) {
		this.partList.addAll(a.partList);
		maxKey = getMaxKey();
		return this;
	}

	public void addPartAnimation(PartAnimation pa) {
		partList.add(pa);
		maxKey = getMaxKey();
	}

	// アニメーションが終了したかを判定するためにkeyの最大値を探索して返すメソッド
	private long getMaxKey() {
		long maxKey = 0;
		int i;

		for (i = 0; i < partList.size(); i++) {
			if (maxKey < partList.get(i).getLastKey()) {
				maxKey = partList.get(i).getLastKey();
			} else
				continue;
		}
		return maxKey;
	}
}
