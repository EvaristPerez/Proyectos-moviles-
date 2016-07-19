using UnityEngine;
using System.Collections;

public class PlayerMovement : MonoBehaviour {

    Animator anim;
	Rigidbody2D rBody;
	
	public float speed=5;
	public VirtualJoystick joystick;


    void Start () {

        anim = GetComponent<Animator>();
		rBody = GetComponent<Rigidbody2D>();

    }

    void Update(){

		bool dead = GetComponent<CharacterCollider>().getIsDead();

        float inputX = joystick.Horizontal();
        float inputY = joystick.Vertical();

	
		Vector2 movement_vector = new Vector2(0f, 0f);

		if ((inputX != 0 || inputY != 0) && dead==false)
        {
			anim.SetBool("parado", false);

            if (inputX > 0.7)
            {
				movement_vector.x = speed;
				anim.SetFloat("velocidadX", 1.0f);


            }

            else if (inputX < -0.7)
            {
				movement_vector.x = -speed;
				anim.SetFloat("velocidadX", -1.0f);
            }

            else
			{
				movement_vector.x = 0f;
				anim.SetFloat("velocidadX", 0.0f);

            }

            if (inputY > 0.7)
            {
				movement_vector.y = speed;
				anim.SetFloat("velocidadY", 1.0f);

            }

            else if (inputY < -0.7)
            {
				movement_vector.y = -speed;
				anim.SetFloat("velocidadY", -1.0f);
            }

            else
            {
				movement_vector.y = 0;
				anim.SetFloat("velocidadY", 0.0f);

            }

            if ((inputX <= 0.7 && inputX >= -0.7) && (inputY <= 0.7 && inputY >= -0.7))
            {
                if (Mathf.Abs(inputX) > Mathf.Abs(inputY))
                {
                    anim.SetFloat("velocidadY", 0.0f);
					movement_vector.y = 0f;


                    if (inputX > 0)
                    {
						movement_vector.x = speed;
						anim.SetFloat("velocidadX", 1.0f);
                    }

                    else
                    {
						movement_vector.x = -speed;
						anim.SetFloat("velocidadX", -1.0f);
                    }
                }

                else
                {
					movement_vector.x = 0f;
					anim.SetFloat("velocidadX", 0.0f);

                    if (inputY > 0)
                    {
						movement_vector.y = speed;
                        anim.SetFloat("velocidadY", 1.0f);
                    }

                    else
					{	
						movement_vector.y = -speed;
						anim.SetFloat("velocidadY", -1.0f);
                    }
                }
            }
        }

        else
        {
			movement_vector.x = 0;
			movement_vector.y = 0;
            anim.SetBool("parado", true);

        }

       rBody.MovePosition(rBody.position + movement_vector * Time.deltaTime);
    }

    void FixedUpdate()
    {
		bool dead = GetComponent<CharacterCollider>().getIsDead();

        float lastInputX = joystick.Horizontal();
        float lastInputY = joystick.Vertical();

		if ((lastInputX != 0 || lastInputY != 0) && dead==false)
        {
            anim.SetBool("parado", false);

            if (lastInputX > 0.7)
            {
                anim.SetFloat("lastX", 1.0f);

            }

            else if (lastInputX < -0.7)
            {
                anim.SetFloat("lastX", -1.0f);
            }

            else
            {
                anim.SetFloat("lastX", 0.0f);

            }

            if (lastInputY > 0.7)
            {
                anim.SetFloat("lastY", 1.0f);

            }

            else if (lastInputY < -0.7)
            {
                anim.SetFloat("lastY", -1.0f);
            }

            else
            {
                anim.SetFloat("lastY", 0.0f);

            }

            if ((lastInputX <= 0.7 && lastInputX >= -0.7) && (lastInputY <= 0.7 && lastInputY >= -0.7))
            {
                if (Mathf.Abs(lastInputX) > Mathf.Abs(lastInputY))
                {
                    anim.SetFloat("lastY", 0.0f);

                    if (lastInputX > 0)
                    {
                        anim.SetFloat("lastX", 1.0f);
                    }

                    else
                    {
                        anim.SetFloat("lastX", -1.0f);
                    }
                }

                else
                {
                    anim.SetFloat("lastX", 0.0f);

                    if (lastInputY > 0)
                    {
                        anim.SetFloat("lastY", 1.0f);
                    }

                    else
                    {
                        anim.SetFloat("lastY", -1.0f);
                    }
                }
            }
        }

        else
        {
            anim.SetBool("parado", true);

			if (dead == true) {

				anim.SetFloat("lastX", 0.0f);
				anim.SetFloat("lastY", 0.0f);

			}

        }
    }


}