<li>
  <slot />
</li>

<style>
  li {
    position: relative;
    display: flex;
    padding: 0.75rem 1.25rem;
    margin-bottom: -1px;
    background-color: #fff;
    border: 1px solid rgba(0, 0, 0, 0.125);
  }

  li:hover {
    background: var(--light-grey);
  }

  li > :global(div) {
    flex: 1 1 0;
  }
</style>
