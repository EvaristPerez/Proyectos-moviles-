using UnityEngine;
using UnityEngine.UI;
using UnityEngine.EventSystems;
 
public class VirtualJoystick : MonoBehaviour, IDragHandler, IPointerDownHandler, IPointerUpHandler
{
    public Image bgImg;
    public Image joystickImg;
    public Vector3 inputDirection { set; get;}

    void Start()
    {
        bgImg = GetComponent<Image>();
        joystickImg = transform.GetChild(0).GetComponent<Image>();
        inputDirection = Vector3.zero;
    }

    public void OnDrag(PointerEventData ped)
    {
        Vector2 pos = Vector2.zero;

		if (RectTransformUtility.ScreenPointToLocalPointInRectangle(bgImg.rectTransform, ped.position, ped.pressEventCamera, out pos) && Time.timeScale == 1.0)
        {
            pos.x = (pos.x / bgImg.rectTransform.sizeDelta.x);
            pos.y = (pos.y / bgImg.rectTransform.sizeDelta.y);

            float x = (bgImg.rectTransform.pivot.x == 1) ? pos.x * 2 + 1 : pos.x * 2 - 1;
            float y = (bgImg.rectTransform.pivot.y == 1) ? pos.y * 2 + 1 : pos.y * 2 - 1;

            inputDirection = new Vector3(x, 0 , y);
            inputDirection = (inputDirection.magnitude > 1) ? inputDirection.normalized : inputDirection;

            joystickImg.rectTransform.anchoredPosition = new Vector3(inputDirection.x * (bgImg.rectTransform.sizeDelta.x/ 3), inputDirection.z * (bgImg.rectTransform.sizeDelta.y / 3));
          
        }
    }

    public void OnPointerDown(PointerEventData ped)
    {
        OnDrag(ped);
    }

    public void OnPointerUp(PointerEventData ped)
    {
        inputDirection = Vector3.zero;
        joystickImg.rectTransform.anchoredPosition = Vector3.zero;
    }

    public float Horizontal()
    {
        if (inputDirection.x != 0)
            return inputDirection.x;

        else
            return Input.GetAxis("Horizontal");
    }

    public float Vertical()
    {
        if (inputDirection.z != 0)
            return inputDirection.z;

        else
            return Input.GetAxis("Vertical");
    }
}
