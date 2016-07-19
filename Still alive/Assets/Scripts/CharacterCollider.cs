using UnityEngine;
using UnityEngine.UI;
using System.Collections;
using UnityEngine.SceneManagement;
using GooglePlayGames;
using UnityEngine.SocialPlatforms;

public class CharacterCollider : MonoBehaviour {

	public Sprite openDoor, closeDoor, medals0, medals1, medals2, medals3;
	public Vector2 initialPosition;
	public Image imageMedals, imageKey, imageMedalsCompleted;
	public Canvas menuCompleted;
	public bool keys, switches;
	public int level;


	int numKeys, numDeath, numMedals;
	Text death;
	bool isDead;
	CanvasGroup groupCompleted;

	void Start () {

		numKeys = 0;
		numMedals = 0;
		numDeath = 0;
		death = GameObject.Find("Muertes").GetComponent<Text>();
		groupCompleted = menuCompleted.GetComponent<CanvasGroup> ();
		isDead = false;
	}

	void OnTriggerEnter2D(Collider2D objectCollider) {

		switch (objectCollider.tag) {

			case "Llave":			
				
				objectCollider.GetComponent<CircleCollider2D> ().enabled = false;
				objectCollider.GetComponent<SpriteRenderer> ().enabled = false;
				imageKey.GetComponent<Image> ().enabled = true;
				numKeys++;
				
				objectCollider.GetComponent<TriggerAudio> ().Play ();

				break;

			case "Interruptor":

				objectCollider.GetComponent<CircleCollider2D> ().enabled = false;
				objectCollider.GetComponent<SpriteRenderer> ().enabled = false;

				objectCollider.GetComponent<TriggerAudio> ().Play ();

				GameObject[] puertasInterruptor = GameObject.FindGameObjectsWithTag ("PuertaInterruptor");
				
				foreach (GameObject pI in puertasInterruptor) {

					pI.GetComponent<SpriteRenderer> ().sprite = openDoor;
					pI.GetComponent<BoxCollider2D> ().enabled = false;

				}
				
				break;

			case "Trampa":

				GameObject[] proyectilesTrampa = GameObject.FindGameObjectsWithTag ("ProyectilTrampa");
				
				foreach (GameObject pT in proyectilesTrampa) {

					pT.GetComponent<RouteTrap> ().startMove();

				}
				
				break;

			case "Trampa2":

				GameObject[] proyectilesTrampa2 = GameObject.FindGameObjectsWithTag ("ProyectilTrampa2");

				foreach (GameObject pT in proyectilesTrampa2) {

					pT.GetComponent<RouteTrap> ().startMove();

				}

				break;

			case "Trampa3":

				GameObject[] proyectilesTrampa3 = GameObject.FindGameObjectsWithTag ("ProyectilTrampa3");

				foreach (GameObject pT in proyectilesTrampa3) {

					pT.GetComponent<RouteTrap> ().startMove();

				}

				break;

			case "RocaFuego":
			case "EnemigoPatrulla":
			case "Proyectil":
			case "Pinchos":
			case "RocaMarron":
			case "RocaGris":
			case "ProyectilTrampa":
			case "ProyectilTrampa2":
			case "ProyectilTrampa3":
							
				reloadMedals ();
				numMedals = 0;
				imageMedals.sprite = medals0;

				if (keys == true) {

					reloadKeys ();
					numKeys = 0;
					imageKey.GetComponent<Image> ().enabled = false;

					reloadDoors ();
				}

				if (switches == true) {

					reloadSwitches ();
					reloadDoorsSwitches ();
				}
				
				if (isDead==false)
				{
					transform.position = initialPosition;
					this.gameObject.GetComponent<TriggerAudio> ().Play ();

					BlinkPlayer (3);
				}

				break;

			case "Medalla":

				numMedals++;
				objectCollider.GetComponent<CircleCollider2D> ().enabled = false;
				objectCollider.GetComponent<SpriteRenderer> ().enabled = false;
				objectCollider.GetComponent<TriggerAudio> ().Play ();

				switch (numMedals) {

					case 1:

						imageMedals.sprite = medals1;
								
						break;

					case 2:

						imageMedals.sprite = medals2;

						break;

					case 3:

						imageMedals.sprite = medals3;

						break;

					default: break;
				}
				
				break;

			case "Final":

				objectCollider.GetComponent<TriggerAudio> ().Play ();

				MySaveGame guardar = SaveGameSystem.LoadGame ("PartidaGuardada") as MySaveGame;

				if (guardar == null) {

					guardar = new MySaveGame ();
				}

				if (numMedals > guardar.getMedallasNivel (level)) {

					guardar.setMedallasNivel (level, numMedals);

				}

				if (numMedals == 3 && (Social.localUser.authenticated)) {

					Social.ReportProgress ("CgkI3c2ekbAIEAIQAg", 100.0f,	(bool success) => {
					});

				}
						
				if (level != 5) {
								
					guardar.setNivelBloqueado (level + 1, false);

				} 

				if(Social.localUser.authenticated && level == 4){
					
						Social.ReportProgress ("CgkI3c2ekbAIEAIQAw", 100.0f,	(bool success) => {
						});
				}

				if(Social.localUser.authenticated && level == 1){

					Social.ReportProgress ("CgkI3c2ekbAIEAIQBA", 100.0f,	(bool success) => {
						});
				}

				SaveGameSystem.SaveGame (guardar, "PartidaGuardada");

				levelCompleted ();

				break;
		}
	}

	void OnCollisionEnter2D(Collision2D coll) {
		
		switch (coll.gameObject.tag) {

			case "Puerta":	
					
				if (numKeys > 0) {

					coll.gameObject.GetComponent<SpriteRenderer> ().sprite = openDoor;
					coll.gameObject.GetComponent<BoxCollider2D> ().enabled = false;
					
					imageKey.GetComponent<Image> ().enabled = false;
					numKeys--;

					coll.gameObject.GetComponent<TriggerAudio> ().Play ();

				}
				
				break;
		}
	}

	void BlinkPlayer(int numBlink)
	{	
		if (Social.localUser.authenticated) {

			Social.ReportProgress ("CgkI3c2ekbAIEAIQAQ", 100.0f,	(bool success) => {
			});
		} 

		isDead = true;
		StartCoroutine( Wait (.3f, numBlink));
	}

	IEnumerator Wait(float seconds, int num)
	{
		int count = 0;

		while (num > count) {

			yield return new WaitForSeconds (0.2f); 
			count++;

			GetComponent<SpriteRenderer> ().enabled = false;
			yield return new WaitForSeconds (seconds); 
			GetComponent<SpriteRenderer> ().enabled = true;
		}

		numDeath++;
		addDeaths ();
		isDead = false;
	}

	void addDeaths()
	{
		death.text = "Muertes:   " + numDeath;
	}

	void reloadMedals() {

		GameObject[] ObjectMedals;
		ObjectMedals = GameObject.FindGameObjectsWithTag("Medalla");

		foreach (GameObject m in ObjectMedals) {

			m.GetComponent<CircleCollider2D> ().enabled = true;
			m.GetComponent<SpriteRenderer> ().enabled = true;
		}
	}

	void reloadKeys() {

		GameObject[] ObjectKeys;
		ObjectKeys = GameObject.FindGameObjectsWithTag("Llave");

		foreach (GameObject m in ObjectKeys) {

			m.GetComponent<CircleCollider2D> ().enabled = true;
			m.GetComponent<SpriteRenderer> ().enabled = true;
		}

	}

	void reloadSwitches() {

		GameObject[] ObjectSwitches;
		ObjectSwitches = GameObject.FindGameObjectsWithTag("Interruptor");

		foreach (GameObject s in ObjectSwitches) {

			s.GetComponent<CircleCollider2D> ().enabled = true;
			s.GetComponent<SpriteRenderer> ().enabled = true;
		}

	}

	void reloadDoors() {

		GameObject[] ObjectDoors;
		ObjectDoors = GameObject.FindGameObjectsWithTag("Puerta");

		foreach (GameObject m in ObjectDoors) {

			m.GetComponent<BoxCollider2D> ().enabled = true;
			m.GetComponent<SpriteRenderer> ().sprite = closeDoor;
		}

	}

	void reloadDoorsSwitches() {

		GameObject[] ObjectDoors;
		ObjectDoors = GameObject.FindGameObjectsWithTag("PuertaInterruptor");

		foreach (GameObject m in ObjectDoors) {

			m.GetComponent<BoxCollider2D> ().enabled = true;
			m.GetComponent<SpriteRenderer> ().sprite = closeDoor;
		}

	}

	void levelCompleted()
	{
		groupCompleted.alpha = 1.0f;
		groupCompleted.interactable = true;
		groupCompleted.blocksRaycasts = true;
		Time.timeScale = 0.0f;

		switch (numMedals) {

		case 1:

			imageMedalsCompleted.sprite = medals1;

			break;

		case 2:

			imageMedalsCompleted.sprite = medals2;

			break;

		case 3:

			imageMedalsCompleted.sprite = medals3;

			break;

		default: break;
		}
	}

	public bool getIsDead()
	{
		return isDead;
	}


}
