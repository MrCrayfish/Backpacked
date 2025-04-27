import clsx from 'clsx';
import Link from '@docusaurus/Link';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import Layout from '@theme/Layout';
import HomepageFeatures from '@site/src/components/HomepageFeatures';

import Heading from '@theme/Heading';
import styles from './index.module.css';

export default function Home() {
  const {siteConfig} = useDocusaurusContext();
  return (
    <Layout
      title={`Backpacked`}
      description="Documentation for Backpacked by MrCrayfish. Learn how to create an addon to add even more backpack customisation!">
      <main>
        <HomepageFeatures />
      </main>
    </Layout>
  );
}
