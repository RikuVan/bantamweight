<div class="form-btns"><slot /></div>

<style>
  @media only screen and (min-width: 600px) {
    .form-btns {
      grid-column: span 2;
    }
  }

  .form-btns {
    margin-left: auto;
  }

  :global(button + button) {
    margin-left: 8px;
  }
</style>
