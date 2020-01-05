# EasyMotion
Similar to vim's EasyMotion plugin for quick controlling of Elastic's UI without using a mouse.

## Code example
To make an UIObject visible and controllable by EasyMotion, inherit the EasyMotionable interface:

```kotlin
class MyUIComponent : UIObject, EasyMotionAble {
    override fun motionTriggers() = arrayOf(
        
    )
}
```