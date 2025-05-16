import clsx from 'clsx';
import Heading from '@theme/Heading';
import styles from './styles.module.css';
import Link from '@docusaurus/Link';

const AddonDocumentationList = [
  {
    title: (
      <>
        Create an Addon <span class="badge badge--warning">Intermediate</span>
      </>
    ),
    link: '/docs/custom-backpack/introduction',
    description: (
      <>
        This guide will show you how to add even more backpacks to Backpacked using either a resource/data packs or by integrating into a mod. This guide will cover registering new backpacks, creating the models, and setting the challenges to unlock backpacks.
      </>
    ),
    action_text: 'View Guide'
  },
]

function Documentation({ title, description, link, action_text }) {
  return (
    <div className={clsx('col col--6 margin-bottom--lg')}>
      <div class="card-demo">
        <div class="card shadow--lw">
          <div class="card__header">
            <h3>{title}</h3>
            <p>{description}</p>
          </div>
          <div class="card__footer">
            <Link
              className="button button--secondary button--lg button--block"
              to={link}>
              {action_text}
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}

export default function HomepageFeatures() {
  return (
    <div>
      <section className={styles.features}>
        <div className="container">
          <Heading as="h1">📦 Addons</Heading>
          <div className="row">
            {AddonDocumentationList.map((props, idx) => (
              <Documentation key={idx} {...props} />
            ))}
          </div>
        </div>
      </section>
    </div>
  );
}
