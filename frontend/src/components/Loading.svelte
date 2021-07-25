<div class="container">
  <div class="loading">
    {#each [...Array(5).keys()].map((i) => i + 1) as version}
      <div class="rect" style="animation-delay: {(version - 1) * (1.2 / 12)}s" />
    {/each}
  </div>
</div>

<style>
  .container {
    display: flex;
    width: 100%;
    justify-content: center;
    align-items: center;
    height: 100%;
  }
  .loading {
    height: 60px;
    width: 60px;
    display: inline-block;
    text-align: center;
    font-size: 10px;
  }
  .rect {
    height: 100%;
    width: 10%;
    display: inline-block;
    margin-right: 4px;
    background-color: var(--orange);
    animation: stretch 1.2s ease-in-out infinite;
  }
  @keyframes stretch {
    0%,
    40%,
    100% {
      transform: scaleY(0.4);
    }
    20% {
      transform: scaleY(1);
    }
  }
</style>
