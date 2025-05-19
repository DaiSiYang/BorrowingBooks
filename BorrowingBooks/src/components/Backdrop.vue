<!-- Backdrop.vue -->
<template>
  <div ref="vantaRef" class="vanta-bg">
    <slot /> <!-- 这里你可以插入页面内容，比如登录表单 -->
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'

const vantaRef = ref(null)
let vantaEffect = null

onMounted(() => {
  console.log('Backdrop 组件已挂载')
  console.log('DOM 元素:', vantaRef.value)
  
  // 确保 THREE 和 VANTA 已加载
  if (typeof window !== 'undefined') {
    console.log('开始加载 Three.js')
    // 动态加载 Three.js
    const threeScript = document.createElement('script')
    threeScript.src = 'https://cdn.jsdelivr.net/npm/three@0.134.0/build/three.min.js'
    document.head.appendChild(threeScript)

    threeScript.onload = () => {
      console.log('Three.js 已加载成功')
      // 加载 Vanta.js
      console.log('开始加载 Vanta.js')
      const vantaScript = document.createElement('script')
      vantaScript.src = 'https://cdn.jsdelivr.net/npm/vanta@0.5.24/dist/vanta.clouds.min.js' // 使用 clouds 效果
      document.head.appendChild(vantaScript)

      vantaScript.onload = () => {
        console.log('Vanta.js 已加载成功')
        console.log('VANTA 对象:', window.VANTA)
        console.log('THREE 对象:', window.THREE)
        
        // 确保 DOM 已渲染
        setTimeout(() => {
          console.log('尝试初始化 Vanta 效果')
          console.log('DOM 元素状态:', vantaRef.value)
          if (vantaRef.value && window.VANTA) {
            try {
              // 使用 CLOUDS 效果，使用第三张图片中的颜色代码
              vantaEffect = window.VANTA.CLOUDS({
                el: vantaRef.value,
                THREE: window.THREE,
                mouseControls: true,
                touchControls: true,
                gyroControls: false,
                minHeight: 200.0,
                minWidth: 200.0,
                backgroundColor: 0xffffff, // 背景颜色 0xffffff
                skyColor: 0x68b8d7,        // 天空颜色 0x68b8d7
                cloudColor: 0xadc1de,       // 云颜色 0xadc1de
                cloudShadowColor: 0x183550, // 云阴影颜色 0x183550
                sunColor: 0xff9919,         // 太阳颜色 0xff9919
                sunGlareColor: 0xff6633,    // 太阳眩光颜色 0xff6633
                sunlightColor: 0xff9933,    // 阳光颜色 0xff9933
                speed: 1.0                  // 速度 1
              })
              console.log('Vanta 效果已初始化', vantaEffect)
            } catch (error) {
              console.error('Vanta 初始化错误:', error)
            }
          } else {
            console.error('Vanta 或 DOM 元素未找到')
            console.log('vantaRef.value:', vantaRef.value)
            console.log('window.VANTA:', window.VANTA)
          }
        }, 500) // 增加延时
      }
      
      vantaScript.onerror = (error) => {
        console.error('Vanta.js 加载失败:', error)
      }
    }
    
    threeScript.onerror = (error) => {
      console.error('Three.js 加载失败:', error)
    }
  }
})

onBeforeUnmount(() => {
  console.log('Backdrop 组件即将卸载')
  if (vantaEffect) {
    vantaEffect.destroy()
    vantaEffect = null
  }
})
</script>

<style scoped>
.vanta-bg {
  width: 100vw;
  height: 100vh;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1; /* 提高 z-index 确保背景可见 */
  pointer-events: none; /* 避免影响内容交互 */
}
</style>