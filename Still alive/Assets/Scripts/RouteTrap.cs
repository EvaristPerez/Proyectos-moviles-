using UnityEngine;
using System.Collections;

public class RouteTrap : MonoBehaviour {

	public Vector2 pointB;
	public float timePath;

	bool move = false;
	Vector2 pointA;

	public void startMove()
	{
		if (move == false) {
			
			pointA = transform.position;
			StartCoroutine (MoveObject (transform, pointA, pointB, timePath));
		}

	}

	IEnumerator MoveObject(Transform thisTransform, Vector2 startPos, Vector2 endPos, float timePath)
	{
		if (move == false) {

			var i = 0.0f;
			var rate = 1.0f / timePath;

			initBullet ();

			while (i < 1.0f) {
				i += Time.deltaTime * rate;
				thisTransform.position = Vector2.Lerp (startPos, endPos, i);
				yield return null; 
			}

			removeBullet ();
		}
	}

	void removeBullet()
	{
		GetComponent<SpriteRenderer> ().enabled = false;
		GetComponent<PolygonCollider2D> ().enabled = false;
		transform.position = pointA;

		move = false;
	}

	void initBullet()
	{
		GetComponent<SpriteRenderer> ().enabled = true;
		GetComponent<PolygonCollider2D> ().enabled = true;

		move = true;
	}


}
