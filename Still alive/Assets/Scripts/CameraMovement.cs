using UnityEngine;
using System.Collections;

public class CameraMovement : MonoBehaviour {

    public Transform personaje;
	public float smooth, maxX, minX, maxY, minY;

	float xPosition, yPosition;

	// Use this for initialization
	void Start () {

	}
	
	// Update is called once per frame
	void Update () {

		xPosition = personaje.position.x;
		yPosition = personaje.position.y;

		if (xPosition > maxX) {

			xPosition = maxX;
		}

		else if (xPosition < minX) {

			xPosition = minX;
		}

		if (yPosition > maxY) {

			yPosition = maxY;
		}

		else if (yPosition < minY) {

			yPosition = minY;
		}
			

		transform.position = Vector3.Lerp(transform.position, new Vector3(xPosition, yPosition, transform.position.z), Time.deltaTime * smooth);
	
	}
}
