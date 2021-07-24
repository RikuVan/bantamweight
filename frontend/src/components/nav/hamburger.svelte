<script lang="ts">
  export let open = false

  function handleClick() {
    open = !open
  }
</script>

<button type="button" class:open on:click={handleClick}>
  <span />
  <span />
  <span />
  <span />
</button>

<style>
  button {
    background: none;
    border-radius: 0;
    border: 0;
    width: 40px;
    height: 30px;
    position: absolute;
    top: 30px;
    right: 20px;
    outline: none;
    transform: rotate(0deg);
    transition: 0.5s ease-in-out;
    z-index: 100;
    cursor: pointer;
  }

  span {
    display: block;
    position: absolute;
    height: 4px;
    width: 100%;
    background: #fff;
    border-radius: 2px;
    opacity: 1;
    left: 0;
    transform: rotate(0deg);
    transition: 0.25s ease-in-out;
  }

  span:nth-child(1) {
    top: 0px;
  }

  button.open span:nth-child(1) {
    top: 18px;
    width: 0%;
    left: 50%;
  }

  span:nth-child(2) {
    top: 0px;
  }

  button.open span:nth-child(2) {
    top: 12px;
    transform: rotate(45deg);
  }

  span:nth-child(3) {
    top: 12px;
  }

  button.open span:nth-child(3) {
    transform: rotate(-45deg);
  }

  span:nth-child(4) {
    top: 24px;
  }

  button.open span:nth-child(4) {
    top: 12px;
    width: 0%;
    left: 50%;
  }
</style>
